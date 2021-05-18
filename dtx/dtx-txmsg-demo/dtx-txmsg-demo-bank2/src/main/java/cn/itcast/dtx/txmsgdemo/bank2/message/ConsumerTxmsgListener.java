package cn.itcast.dtx.txmsgdemo.bank2.message;

import cn.itcast.dtx.txmsgdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank2.service.AccountInfoService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 20:24 ]
 * @since :[ 1.0.0 ]
 */
@Component
@RocketMQMessageListener(topic = "topic-txmsg", consumerGroup = "consumer-group-txmsg-bank2")
@Slf4j
public class ConsumerTxmsgListener implements RocketMQListener<String> {

    @Autowired
    AccountInfoService accountInfoService;

    @Override
    public void onMessage(String s) {
        log.info("开始消费消息, msg: {}", s);
        JSONObject message = JSONObject.parseObject(s);
        String accountChangeStr = message.getString("accountChange");
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChangeStr, AccountChangeEvent.class);
        accountChangeEvent.setAccountNo("2");
        accountInfoService.addAccountInfoBalance(accountChangeEvent); // 如果抛出异常怎么办?
        if(accountChangeEvent.getAmount() == 4) {
            throw new RuntimeException("Bank2 人工制造异常");
        }
    }
}
