package org.ccg.test.json.check;

import java.util.Date;

/**
 * 期望时间与当前时间校验器，默认误差是1分钟内
 */
public class TimeCheckNow extends TimeCheck {
    // 默认误差是1分钟
    private final long diffCount;
    @Override
    boolean checkDate(Date expectDate, Date actualDate) {
        if (actualDate != null) {
            long time = System.currentTimeMillis() - actualDate.getTime();
            return time >= 0 && time < diffCount;
        }
        return false;
    }

    public TimeCheckNow(String... keys) {
        super(keys);
        this.diffCount = 60 * 1000;
    }

    public TimeCheckNow(long diffCount, String... keys) {
        super(keys);
        this.diffCount = diffCount;
    }
}
