package cn.itcast.dtx.txmsgdemo.bank2.service.impl;

import cn.itcast.dtx.txmsgdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank2.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 20:16 ]
 * @since :[ 1.0.0 ]
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    AccountInfoDao accountInfoDao;

    @Override
    @Transactional
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent) {
        log.info("bank2 更新本地账号, 账号: {}, 金额: {}", accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount());
        // 幂等性校验
        if (accountInfoDao.isExistTx(accountChangeEvent.getTxNo()) > 0) {
            return;
        }

        // 给账户加钱
        int i = accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount());
        if(i != 1) {
            throw new RuntimeException("加钱失败");
        }
        // 记录事务
        int j = accountInfoDao.addTx(accountChangeEvent.getTxNo());
        if(j != 1) {
            throw new RuntimeException("事务记录失败");
        }
    }
}
