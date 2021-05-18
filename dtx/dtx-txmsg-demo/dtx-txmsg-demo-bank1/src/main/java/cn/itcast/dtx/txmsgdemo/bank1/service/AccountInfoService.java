package cn.itcast.dtx.txmsgdemo.bank1.service;

import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 17:01 ]
 * @since :[ 1.0.0 ]
 */
public interface AccountInfoService {

    /**
     * 向MQ发送转账消息
     *
     * @param accountChangeEvent
     */
    void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent);

    /**
     * 更新账户, 扣减金额
     *
     * @param accountChangeEvent
     */
    void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent);
}
