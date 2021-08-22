package app.egs.shop.domain.util;

import java.util.Random;
import java.util.UUID;

public class RandomGenerator {

    public static String iban() {
        StringBuilder start = new StringBuilder("AM");
        Random value = new Random();
        int r1 = value.nextInt(10);
        int r2 = value.nextInt(10);
        start.append(r1).append(r2).append("-");
        int count = 0;
        int n = 0;
        for (int i = 0; i < 12; i++) {
            if (count == 4) {
                start.append("-");
                count = 0;
            } else
                n = value.nextInt(10);
            start.append(n);
            count++;
        }
        return start.toString();
    }

    public static String stan() {
        return UUID.randomUUID().toString();
    }


}
