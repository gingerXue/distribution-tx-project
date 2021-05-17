package cn.itcast.dtx.tccdemo.bank2.controller;

import cn.itcast.dtx.tccdemo.bank2.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/17 15:29 ]
 * @since :[ 1.0.0 ]
 */
@RestController
public class AccountInfoController {

    @Autowired
    AccountInfoService accountInfoService;

    @GetMapping("/transfer")
    public Boolean transfer(@RequestParam("amount") Double amount) {
        accountInfoService.updateAccountBalance("2", amount);
        return true;
    }
}
