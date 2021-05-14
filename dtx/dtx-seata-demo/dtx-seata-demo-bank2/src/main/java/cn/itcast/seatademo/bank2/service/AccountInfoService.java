package cn.itcast.seatademo.bank2.service;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 17:07 ]
 * @since :[ 1.0.0 ]
 */
public interface AccountInfoService {
    void updateAccountBalance(String accountNo, Double amount);
}
