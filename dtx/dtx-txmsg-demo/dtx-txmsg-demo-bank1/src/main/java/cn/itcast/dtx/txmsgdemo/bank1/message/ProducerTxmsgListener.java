package cn.itcast.dtx.txmsgdemo.bank1.message;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * RocketMQ本地事务监听器
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 17:46 ]
 * @since :[ 1.0.0 ]
 */
@Component
@Slf4j
// txProducerGroup的值需要和生产者发消息时指定的生产组保持一致
@RocketMQTransactionListener(txProducerGroup = "producer-group-txmsg-bank1")
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {

    @Autowired
    AccountInfoService accountInfoService;

    @Autowired
    AccountInfoDao accountInfoDao;
    /**
     * MQ 接收到消息之后的回调方法
     *
     * @param message
     * @param o
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        // mq接收到消息之后, 执行本地事务
        log.info("MQ成功接收到消息的回调");
        try {
            // 扣减金额
            accountInfoService.doUpdateAccountBalance(handleMessage(message));
            // 返回提交状态, MQ就会将消息变为可消费状态
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            e.printStackTrace();
            // MQ会将消息丢弃
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    private AccountChangeEvent handleMessage(Message message) {
        String msgStr = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(msgStr);
        String accountChangeStr = jsonObject.getString("accountChange");
        return JSONObject.parseObject(accountChangeStr, AccountChangeEvent.class);
    }

    /**
     * MQ 回查本地事务状态的方法
     *
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("MQ回查本地事务状态");
        AccountChangeEvent accountChangeEvent = handleMessage(message); // 解析消息
        // 判断本地事务状态是否已经提交
        if(accountInfoDao.isExistTx(accountChangeEvent.getTxNo()) > 0) {
            // 本地事务未提交
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}
