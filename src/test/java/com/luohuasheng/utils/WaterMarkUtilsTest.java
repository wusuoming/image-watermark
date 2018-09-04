package com.luohuasheng.utils;

import org.junit.Test;

public class WaterMarkUtilsTest {
    WaterMarkUtils bwm = new WaterMarkUtils();

    @Test
    public void encodeforText() {
        bwm.encode("gakki.png", "hello", "gakki-wm-text.png", true);
    }

    @Test
    public void decodeForText() {
        bwm.decode("gakki-wm-text.png", "gakki-text-dc.png");

    }

    @Test
    public void encodeforImg() {
        bwm.encode("gakki.png", "wm.png", "gakki-wm-img.png", false);
    }

    @Test
    public void decodeForImg() {
        bwm.decode("gakki.png", "gakki-wm-img.png", "gakki-img-dc.png");

    }
}