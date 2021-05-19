package cn.itcast.dtx.txmsgdemo.bank1.service.impl;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.entity.AccountInfo;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import cn.itcast.dtx.txmsgdemo.util.ThreadPoolTool;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 17:05 ]
 * @since :[ 1.0.0 ]
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    AccountInfoDao accountInfoDao;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    DataSourceTransactionManager transactionManager;

    /**
     * 向RocketMQ发送转账消息
     *
     * @param accountChangeEvent
     */
    @Override
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {

        // 将消息转成json
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountChange", accountChangeEvent);
        // 将json转换成Message
        Message<JSONObject> msg = MessageBuilder.withPayload(jsonObject).build();
        /*
         * 发送一条事务消息
         * sendMessageInTransaction参数
         * txProducerGroup 生产组
         * destination 消息发送到mq的那个topic中
         * message 要发送的消息
         * arg 额外参数
         */
        rocketMQTemplate.sendMessageInTransaction("producer-group-txmsg-bank1", "topic-txmsg", msg, null);
    }

    /**
     * 本地事务: 扣减张三账户金额
     *
     * @param accountChangeEvent
     */
    @Transactional
    @Override
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        // 幂等判断
        // 该幂等判断存在并发问题
        // 如果两个同样的请求同时执行这个方法, 其中一个方法还没有记录扣减日志 那么就会导致幂等判断失效
        int existTx = accountInfoDao.isExistTx(accountChangeEvent.getTxNo());
        if (existTx > 0) {
            return;
        }
        // 扣减金额
        int i = accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount() * -1);
        if (i <= 0) {
            throw new RuntimeException("扣减金额失败");
        }
        // 记录扣减日志
        accountInfoDao.addTx(accountChangeEvent.getTxNo());
        if (accountChangeEvent.getAmount() == 10) {
            throw new RuntimeException("人工制造异常");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void insertAccountInfo(AccountInfo accountInfo) {
        try {
            accountInfoDao.addAccount(accountInfo);
            if (accountInfo.getAccountBalance() == 10) {
                throw new RuntimeException("新增用户失败, accountNo: " + accountInfo.getAccountNo());
            }
        } catch (Exception e) {
            log.error("插入异常", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
        }
    }

    @Override
    public void insertAccountInfoBatch(List<AccountInfo> accountInfos) {
        try {
            int threadCount = 3; // 线程数量
            Map params = new HashMap();
            params.put("accountInfoDao", accountInfoDao); // 把dao层放到params中, 不然在ThreadTask中注不进去, 因为是通过反射创建的实例
            ThreadPoolTool.executeTask(transactionManager, accountInfos, threadCount, params, AddAccountInfoBatch.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
