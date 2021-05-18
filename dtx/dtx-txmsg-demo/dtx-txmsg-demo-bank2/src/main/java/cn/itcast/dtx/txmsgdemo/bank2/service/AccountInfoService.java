package cn.itcast.dtx.txmsgdemo.bank2.service;

import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 20:13 ]
 * @since :[ 1.0.0 ]
 */
public interface AccountInfoService {

    /**
     * 给账户加钱
     * @param accountChangeEvent
     */
    void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);
}
