package cn.itcast.dtx.tccdemo.bank1.spring;

import org.springframework.stereotype.Component;

/**
 * 调用远程服务失败的降级处理
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/14 16:48 ]
 * @since :[ 1.0.0 ]
 */
@Component
public class Bank2ClientFallback implements Bank2Client{
    @Override
    public Boolean transfer(Double amount) {
        return false;
    }
}
