package cn.itcast.dtx.txmsgdemo.util;

import lombok.Data;

/**
 * 回滚标记类
 *
 * @author :[ xuejz ]
 * @createTime :[ 2021/5/19 17:51 ]
 * @since :[ 1.0.0 ]
 */
@Data
public class RollBack {

    public RollBack(boolean needRollBack) {
        this.needRollBack = needRollBack;
    }

    private boolean needRollBack;
}
