package cn.itcast.seatademo.bank2.controller;

import cn.itcast.seatademo.bank2.service.AccountInfoService;
import io.seata.core.context.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 17:10 ]
 * @since :[ 1.0.0 ]
 */
@RestController
public class Bank2Controller {

    @Autowired
    AccountInfoService accountInfoService;

    @GetMapping("/transfer")
    public String transfer(Double amount) {
        accountInfoService.updateAccountBalance("2", amount);
        if (amount == 3) {
            throw new RuntimeException("李四 - 人为制造异常, xid: " + RootContext.getXID());
        }
        return "transfer success ! Bank2" + amount;
    }
}
