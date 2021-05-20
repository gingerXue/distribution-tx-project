package cn.itcast.dtx.txmsgdemo.bank1.service.impl;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.entity.AccountInfo;
import cn.itcast.dtx.txmsgdemo.util.RollBack;
import cn.itcast.dtx.txmsgdemo.util.ThreadTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/19 20:42 ]
 * @since :[ 1.0.0 ]
 */
@Slf4j
public class AddAccountInfoBatch extends ThreadTask {

    /**
     * 要处理的数据
     */
    private List<AccountInfo> data;

    /**
     * 无法通过spring的依赖注入来注入, 需要通过params来获得
     */
    private AccountInfoDao accountInfoDao;

    public AddAccountInfoBatch(CountDownLatch childMonitor,
                               CountDownLatch mainMonitor,
                               BlockingDeque resultList,
                               RollBack rollBack,
                               Object obj,
                               Map params,
                               DataSourceTransactionManager transactionManager) {
        super(childMonitor, mainMonitor, resultList, rollBack, params, obj, transactionManager);
        data = (List<AccountInfo>) obj;
    }

    @Override
    public void initParam() {
        accountInfoDao = (AccountInfoDao) getParam("accountInfoDao");
    }

    @Override
    public boolean processTask() {
        log.info("子线程[{}]执行业务逻辑", Thread.currentThread().getName());
        try {
            if (data.get(0).getAccountBalance() == 10) {
                throw new RuntimeException("人工制造异常 - 回滚");
            }
            int i = accountInfoDao.addAccountBatch(data);
            return i == data.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
