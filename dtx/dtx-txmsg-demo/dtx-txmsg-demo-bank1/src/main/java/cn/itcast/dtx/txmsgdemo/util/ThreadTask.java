package cn.itcast.dtx.txmsgdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;

/**
 * 子线程任务执行类
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/19 19:55 ]
 * @since :[ 1.0.0 ]
 */
@Slf4j
public abstract class ThreadTask implements Runnable {

    /**
     * 监控子线程任务的执行
     */
    private CountDownLatch childMonitor;

    /**
     * 监控主线程
     */
    private CountDownLatch mainMonitor;

    /**
     * 子线程执行结果
     */
    private BlockingDeque<Boolean> resultList;

    /**
     * 回滚标记类
     */
    private RollBack rollBack;

    private Map params;

    private Object obj;

    protected DataSourceTransactionManager transactionManager;

    protected TransactionStatus txStatus;

    public ThreadTask(CountDownLatch childMonitor,
                      CountDownLatch mainMonitor,
                      BlockingDeque<Boolean> resultList,
                      RollBack rollBack,
                      Map params,
                      Object obj,
                      DataSourceTransactionManager transactionManager) {
        this.childMonitor = childMonitor;
        this.mainMonitor = mainMonitor;
        this.resultList = resultList;
        this.rollBack = rollBack;
        this.params = params;
        this.obj = obj;
        this.transactionManager = transactionManager;
        initParam();
    }

    public abstract void initParam();

    /**
     * 执行任务, 返回false表示任务执行失败, 需要回滚
     */
    public abstract boolean processTask();
    /**
     * 事务回滚
     */
    private void rollback() {
        log.info("子线程[{}]开始回滚", Thread.currentThread().getName());
        transactionManager.rollback(txStatus);
    }

    /**
     * 事务提交
     */
    private void commit() {
        log.info("子线程[{}]提交事务", Thread.currentThread().getName());
        transactionManager.commit(txStatus);
    }

    protected Object getParam(String key) {
        return params.get(key);
    }

    @Override
    public void run() {
        log.info("子线程[{}]开始执行任务", Thread.currentThread().getName());
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txStatus = transactionManager.getTransaction(def);
        Boolean result = processTask();
        //向队列中添加处理结果
        resultList.offer(result);
        // 使用childMonitor.countDown()释放子线程锁定
        // 同时使用mainMonitor.await();阻塞子线程，将程序的控制权交还给主线程。
        childMonitor.countDown();
        try {
            //等待主线程的判断逻辑执行完，执行下面的是否回滚逻辑
            mainMonitor.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        // 主线程检查子线程执行任务的结果，若有失败结果出现，主线程标记状态将告知子线程回滚，然后使用mainMonitor.countDown();
        // 将程序控制权再次交给子线程，子线程检测回滚标志，判断是否回滚。
        log.info("主线程的判断逻辑执行完成, 子线程[{}]继续执行剩下的任务", Thread.currentThread().getName());
        if(rollBack.isNeedRollBack()) {
            rollback();
        } else {
            commit();
        }
    }
}
