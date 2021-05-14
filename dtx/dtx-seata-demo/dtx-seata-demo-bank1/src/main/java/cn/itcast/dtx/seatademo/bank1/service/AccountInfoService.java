package cn.itcast.dtx.seatademo.bank1.service;

/**
 * bank1 账户信息Service
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 16:23 ]
 * @since :[ 1.0.0 ]
 */
public interface AccountInfoService {
    void updateAccountBalance(String accountNo, Double amount);
}
