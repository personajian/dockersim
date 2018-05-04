package org.example;

import java.util.Random;

/**
 * @Author personajian
 * @Date 2018/4/30 0030 21:54
 */
public class RandomNum {
    public static void main(String[] args) {
/*
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Double r = (random.nextInt(39) + 1)/10.0;
            System.out.println(r);
        }
*/
        Double d = 4000/6000.0;
        System.out.println((int)Math.ceil(d));
    }
}
