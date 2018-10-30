package com.luohuasheng.utils;

import com.luohuasheng.service.WaterMarkService;
import org.junit.Test;

public class WaterMarkServiceTest {
    WaterMarkService bwm = new WaterMarkService();

    @Test
    public void encodeforText() {
        bwm.encode("img/gakki.png", "hello", "img/gakki-wm-text.png", true, 3);
    }

    @Test
    public void decodeForText() {
        bwm.decode("img/gakki-wm-text.png", "img/gakki-text-dc.png");

    }

    @Test
    public void encodeforImg() {
        bwm.encode("img/gakki.png", "img/wm.png", "img/gakki-wm-img2.png", false, 3);
    }

    @Test
    public void decodeForImg() {
        bwm.decode("img/gakki.png", "img/gakki-wm-img.png", "img/gakki-img-dc2.png");

    }
}