package cn.itcast.dtx.txmsgdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具类
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/19 17:38 ]
 * @since :[ 1.0.0 ]
 */
@Slf4j
public class ThreadPoolTool {

    /**
     * 执行多线程任务
     *
     * @param transactionManager 事务管理器
     * @param data               需要执行的数据集合
     * @param threadCount        核心线程数
     * @param params
     * @param clazz
     */
    public static void executeTask(DataSourceTransactionManager transactionManager, List data, int threadCount, Map params, Class clazz) {
        if (data == null || data.size() == 0) {
            return;
        }
        int batch = 0;
        // 创建指定线程数量的线程池
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        //监控子线程的任务执行
        CountDownLatch childMonitor = new CountDownLatch(threadCount);
        //监控主线程，是否需要回滚
        CountDownLatch mainMonitor = new CountDownLatch(1);
        //存储任务的返回结果，返回true表示不需要回滚，反之，则回滚
        BlockingDeque<Boolean> results = new LinkedBlockingDeque<>(threadCount);
        RollBack rollback = new RollBack(false); // 回滚状态
        try {
            LinkedBlockingQueue<List<?>> linkedBlockingQueue = splitQueue(data, threadCount);
            while (true) {
                // 主线程将任务分发给子线程
                List taskList = linkedBlockingQueue.poll();
                batch++;
                params.put("batch", batch);
                if (taskList == null) break;
                Constructor constructor = clazz.getConstructor(CountDownLatch.class,
                        CountDownLatch.class,
                        BlockingDeque.class,
                        RollBack.class,
                        Object.class,
                        Map.class,
                        DataSourceTransactionManager.class);
                ThreadTask taskThread =
                        (ThreadTask) constructor.newInstance(childMonitor, mainMonitor, results, rollback, taskList, params, transactionManager);
                executor.execute(taskThread);
            }
            // 使用childMonitor.await();阻塞主线程，等待所有子线程处理向数据库中插入的业务。
            childMonitor.await();
            log.info("主线程开始执行任务");
            // 根据返回结果来确定是否回滚
            for (int i = 0; i < threadCount; i++) {
                boolean result = (boolean) results.poll();
                if (!result) {
                    rollback.setNeedRollBack(true); // 需要回滚
                    break;
                }
            }
            // 主线程检查子线程执行任务的结果，若有失败结果出现，主线程标记状态告知子线程回滚，然后使用mainMonitor.countDown();
            // 将程序控制权再次交给子线程，子线程检测回滚标志，判断是否回滚。
            mainMonitor.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }

    /**
     * 分割队列, 队列中存放的是每一批次要处理的数据
     *
     * @param data        需要执行的数据集合
     * @param threadCount 核心线程数
     * @return
     */
    private static LinkedBlockingQueue<List<?>> splitQueue(List data, int threadCount) {
        LinkedBlockingQueue<List<?>> queue = new LinkedBlockingQueue<>();
        int dataTotalLen = data.size();
        int batchSize = dataTotalLen / threadCount; // 一批次处理多少数据
        int start, end;
        for (int i = 0; i < threadCount; i++) {
            start = i * batchSize; // data分割开始下标
            end = (i + 1) * batchSize; // data分割结束下表
            if (i < threadCount - 1) {
                queue.offer(data.subList(start, end));
            } else {
                queue.offer(data.subList(start, dataTotalLen));
            }
        }
        return queue;
    }
}
