package cn.itcast.dtx.seatademo.bank1.spring;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * bank2的微服务远程调用接口
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 16:42 ]
 * @since :[ 1.0.0 ]
 */
@FeignClient(value = "seata-demo-bank2", fallback = Bank2ClientFallback.class)
public interface Bank2Client {

    /**
     * 转账给bank2的接口
     * @param amount
     * @return
     */
    @GetMapping("/bank2/transfer")
    String transfer(@RequestParam("amount") Double amount);
}
