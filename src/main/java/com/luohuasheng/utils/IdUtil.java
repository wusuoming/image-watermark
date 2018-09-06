package com.luohuasheng.utils;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdUtil {

    private static IdGenerator idGenerator = new SnowflakeIdGenerator(1);
    private static SnWorker snWorker = new SnWorker(1, 1);

    public static synchronized Long getId() {
        return idGenerator.nextId();
    }

    public static synchronized String getLongId() {
        return idGenerator.nextId(4);
    }

    public static synchronized String getSn() {
        Long thisTime = Long.parseLong(new SimpleDateFormat("yyyyMMdd")
                .format(new Date()));
        BigInteger bigInteger = new BigInteger(thisTime.toString());
        bigInteger = bigInteger.multiply(
                BigInteger.valueOf((long) Math.pow(10, 10))).add(
                BigInteger.valueOf((long) (snWorker.nextId() % 10000000000L)));
        return bigInteger.toString();
    }


}
