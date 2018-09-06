package com.luohuasheng.utils;

interface IdGenerator {

    /**
     * 获取ID
     *
     * @return id值
     */
    Long nextId();

    /**
     * 获取ID
     *
     * @return id值
     */
    String nextId(int addPos);
}
