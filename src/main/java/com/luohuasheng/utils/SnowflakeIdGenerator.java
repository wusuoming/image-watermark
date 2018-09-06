package com.luohuasheng.utils;


import java.math.BigInteger;

class SnowflakeIdGenerator implements IdGenerator {

    private final long workerId;
    private final static long twepoch = 1361753741828L;
    private long sequence = 0L;
    private final static long workerIdBits = 4L;
    public final static long maxWorkerId = -1L ^ -1L << workerIdBits;
    private final static long sequenceBits = 10L;

    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits;
    public final static long sequenceMask = -1L ^ -1L << sequenceBits;

    private long lastTimestamp = -1L;

    @Override
    public Long nextId() {
        long timestamp = this.timeGen();

        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        this.lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << workerIdShift)
                | (this.sequence);

        return nextId;
    }

    public SnowflakeIdGenerator(final long workerId) {
        super();
        if (workerId > SnowflakeIdGenerator.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                    SnowflakeIdGenerator.maxWorkerId));
        }
        this.workerId = workerId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }

        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    @Override
    public String nextId(int addPos) {
        Long id = nextId();
        BigInteger bigInteger = new BigInteger(id.toString());
        bigInteger = bigInteger.multiply(BigInteger.valueOf((long) Math.pow(10, addPos)))
                .add(BigInteger.valueOf((long) ((Math.random() * 9 + 1) * Math.pow(10, addPos))));
        return bigInteger.toString();
    }
}
