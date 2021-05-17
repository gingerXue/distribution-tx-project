package cn.itcast.dtx.tccdemo.bank2.service.impl;

import cn.itcast.dtx.tccdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.tccdemo.bank2.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/17 11:42 ]
 * @since :[ 1.0.0 ]
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    AccountInfoDao accountInfoDao;


    /**
     * 更新账户信息
     * 1. 需保证幂等性
     * 2. 防止悬挂
     * 3. 更新账户信息(如果是加钱,那么需要把加钱的逻辑放在confirm中)
     *
     * @param accountNo
     * @param amount
     */
    @Override
    @Hmily(cancelMethod = "accountTxCancel", confirmMethod = "accountTxConfirm")
    public void updateAccountBalance(String accountNo, Double amount) {
        // 幂等性检查
        log.info("bank2 try begin");
        log.info("bank2 try is empty");
        log.info("bank2 try end");
    }

    /**
     * confirm方法
     * 1. 幂等校验
     * 2. 正式向bank2账户新增金额
     */
    @Transactional
    public void accountTxConfirm(String accountNo, Double amount) {
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("bank2 confirm begin");
        // 幂等校验
        if(accountInfoDao.isExistConfirm(transId) > 0) {
            log.info("bank2 confirm 已经执行过, 无需再次执行");
            return;
        }
        // 向bank2账户新增金额
        if(accountInfoDao.addAccountBalance(accountNo, amount) <= 0) {
            throw new RuntimeException("bank2 新增金额失败, xid: " + transId);
        }
        // 记录confirm
        if(accountInfoDao.addConfirm(transId) <= 0) {
            throw new RuntimeException("bank2 记录confirm失败");
        }
        log.info("bank2 confirm end");
    }

    /**
     * cancel方法
     */
    public void accountTxCancel(String accountNo, Double amount) {
        log.info("bank2 cancel begin");
        log.info("bank2 cancel is empty");
        log.info("bank2 cancel end");
    }
}
