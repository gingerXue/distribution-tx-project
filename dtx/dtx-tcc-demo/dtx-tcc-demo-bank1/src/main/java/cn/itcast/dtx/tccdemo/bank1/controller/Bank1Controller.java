package cn.itcast.dtx.tccdemo.bank1.controller;

import cn.itcast.dtx.tccdemo.bank1.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/17 15:16 ]
 * @since :[ 1.0.0 ]
 */
@RestController
public class Bank1Controller {

    @Autowired
    AccountInfoService accountInfoService;

    @GetMapping("/transfer")
    public String transfer(@RequestParam("amount") Double amount) {
        accountInfoService.updateAccountBalance("1", amount);
        return "转账成功";
    }
}
