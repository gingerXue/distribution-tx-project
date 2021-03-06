package cn.itcast.dtx.txmsgdemo.bank1.controller;

import cn.itcast.dtx.txmsgdemo.bank1.entity.AccountInfo;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * describe
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/18 18:12 ]
 * @since :[ 1.0.0 ]
 */
@RestController
@Slf4j
public class AccountInfoController {

    @Autowired
    AccountInfoService accountInfoService;

    @GetMapping("/transfer")
    public String transfer(@RequestParam("amount") Double amount) {
        String tx_no = UUID.randomUUID().toString();
        AccountChangeEvent accountChangeEvent = new AccountChangeEvent("1", amount, tx_no);
        accountInfoService.sendUpdateAccountBalance(accountChangeEvent);
        return "转账成功";
    }

    /**
     * 测试多线程事务控制
     * @param accountInfos
     * @return
     */
    @PostMapping("/testBatch")
    public String testMutiThreadTx(@RequestBody List<AccountInfo> accountInfos) {
        accountInfoService.insertAccountInfoBatch(accountInfos);
        return "ok";
    }

}
