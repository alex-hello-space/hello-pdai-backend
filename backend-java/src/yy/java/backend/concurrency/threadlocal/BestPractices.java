package yy.java.backend.concurrency.threadlocal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description
 */
public class BestPractices {
    public static void main(String[] args) {
        String res = DateUtils.df.get().format(new Date());
        System.out.println(res);
    }

    static public class DateUtils {
        public static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd");
            }
        };
    }
}
