package yy.java.backend.concurrency.keywords;

/**
 * @author yyHuangfu
 * @create 2024/6/16
 * @description
 */

public class Finals {
    final int i1 = 1;
    final int i2; //空白final

    public Finals() {
        i2 = 1;
    }

    public Finals(int x) {
        this.i2 = x;
    }
}
