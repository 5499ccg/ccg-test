package org.ccg.test.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParseUtil {
    private static final String[] SUPPORTED_PATTERNS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd"
    };

    /**
     * 解析日期字符串
     * @param dateStr 日期字符串
     * @return 解析后的 Date 对象，解析失败返回 null
     */
    public static Date parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        for (String pattern : SUPPORTED_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf.parse(dateStr);
            } catch (Exception e) {
                // 尝试下一个格式
            }
        }
        return null;
    }

    /**
     * 将 Date 格式化为 yyyy-MM-dd HH:mm:ss 格式
     * @param date Date 对象
     * @return 格式化后的字符串，如果 date 为 null 返回 null
     */
    public static String formatDateToStandard(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
