package cn.itcast.dtx.seatademo.bank1.controller;

import cn.itcast.dtx.seatademo.bank1.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 16:52 ]
 * @since :[ 1.0.0 ]
 */
@RestController
public class Bank1Controller {

    @Autowired
    AccountInfoService accountInfoService;

    // 张三转账
    @GetMapping("/transfer")
    public String transfer(Double amount) {
        accountInfoService.updateAccountBalance("1", amount);
        return "bank1" + amount;
    }
}
