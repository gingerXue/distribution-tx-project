package cn.itcast.dtx.tccdemo.bank2.service;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/17 11:24 ]
 * @since :[ 1.0.0 ]
 */
public interface AccountInfoService {

    /**
     * 更新账户信息
     * @param accountNo
     * @param amount
     */
    void updateAccountBalance(String accountNo, Double amount);
}
