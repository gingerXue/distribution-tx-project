package cn.itcast.dtx.tccdemo.bank1.service.impl;

import cn.itcast.dtx.tccdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.tccdemo.bank1.service.AccountInfoService;
import cn.itcast.dtx.tccdemo.bank1.spring.Bank2Client;
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

    @Autowired
    Bank2Client bank2Client;

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
    @Transactional
    @Hmily(cancelMethod = "accountTxConfirm", confirmMethod = "accountTxCancel")
    public void updateAccountBalance(String accountNo, Double amount) {
        // 幂等性检查
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId(); // 获取全局事务下的本地事务编号
        log.info("bank1 try begin");
        if (accountInfoDao.isExistTry(transId) > 0) {
            // try记录存在, 不用再执行一遍了
            log.info("try记录已存在, 无需在执行(幂等), xid: {}", transId);
            return;
        }
        // 悬挂检查
        if (accountInfoDao.isExistCancel(transId) > 0 || accountInfoDao.isExistConfirm(transId) > 0) {
            // cancel或者confirm记录已存在, 不用再执行try方法了
            log.info("cancel或者confirm记录已存在, 不用再执行try方法了(悬挂检查), xid: {}", transId);
            return;
        }
        // 更新账户信息(场景: 给张三扣钱)
        if (accountInfoDao.subtractAccountBalance(accountNo, amount) <= 0) {
            // 更新失败
            throw new RuntimeException("扣款失败");
        }
        // 更新成功后, 新增一条try记录
        accountInfoDao.addTry(transId);

        // 远程调用bank2接口进行转账
        if (!bank2Client.transfer(amount)) {
            // 更新失败
            throw new RuntimeException("bank1调用bank2 转账失败, xid: " + transId);
        }
        log.info("bank1 try end");
    }

    /**
     * confirm方法
     */
    public void accountTxConfirm() {
        log.info("bank1 confirm begin");
        log.info("bank1 confirm is empty");
        log.info("bank1 confirm end");
    }

    /**
     * cancel方法
     * 回滚 需要将扣款金额增加回去
     */
    @Transactional
    public void accountTxCancel(String accountNo, Double amount) {
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId(); // 获取全局事务下的本地事务编号
        log.info("bank1 cancel begin");
        // 幂等校验
        if (accountInfoDao.isExistCancel(transId) > 0) {
            log.info("cancel记录已存在, 幂等, xId: {}", transId);
        }
        // 空回滚检查, 判断try是否执行过了
        if (accountInfoDao.isExistTry(transId) == 0) {
            // try记录不存在, 属于空回滚状态
            log.info("try 记录不存在, 属于空回滚状态, xid: {}", transId);
            return;
        }
        // 回滚, 增加可用余额
        if (accountInfoDao.addAccountBalance(accountNo, amount) <= 0) {
            throw new RuntimeException("回滚失败");
        }
        // 插入cancel记录(保证幂等)
        accountInfoDao.addCancel(transId);
        log.info("bank1 cancel end");
    }
}
