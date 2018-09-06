package com.luohuasheng.utils;

import com.luohuasheng.service.WaterMarkService;
import org.junit.Test;

public class WaterMarkServiceTest {
    WaterMarkService bwm = new WaterMarkService();

    @Test
    public void encodeforText() {
        bwm.encode("gakki.png", "hello", "gakki-wm-text.png", true, 3);
    }

    @Test
    public void decodeForText() {
        bwm.decode("gakki-wm-text.png", "gakki-text-dc.png");

    }

    @Test
    public void encodeforImg() {
        bwm.encode("gakki.png", "wm.png", "gakki-wm-img2.png", false, 3);
    }

    @Test
    public void decodeForImg() {
        bwm.decode("gakki.png", "gakki-wm-img2.png", "gakki-img-dc2.png");

    }
}