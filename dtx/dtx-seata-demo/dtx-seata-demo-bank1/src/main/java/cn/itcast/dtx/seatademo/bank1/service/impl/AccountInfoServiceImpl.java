package cn.itcast.dtx.seatademo.bank1.service.impl;

import cn.itcast.dtx.seatademo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.seatademo.bank1.service.AccountInfoService;
import cn.itcast.dtx.seatademo.bank1.spring.Bank2Client;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 16:37 ]
 * @since :[ 1.0.0 ]
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    private AccountInfoDao accountInfoDao;

    @Autowired
    private Bank2Client bank2Client;

    @GlobalTransactional
    @Transactional
    @Override
    public void updateAccountBalance(String accountNo, Double amount) {
        accountInfoDao.updateAccountBalance(accountNo, amount * -1); // 扣张三的账户余额
        // 远程调用bank2的微服务 给李四的账户余额增加金额
        String transferResult = bank2Client.transfer(amount);
        if(transferResult.equals("fallback")) {
            // 调用微服务失败, 事务回滚
            throw new RuntimeException("call bank2 transfer service failed");
        }
        if(amount == 5) {
            throw new RuntimeException("张三 - 人为制造异常, XID: " + RootContext.getXID());
        }
    }
}
