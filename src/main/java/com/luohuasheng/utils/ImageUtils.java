package com.luohuasheng.utils;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageUtils {

    private static OpenCVFrameConverter.ToMat matConv = new OpenCVFrameConverter.ToMat();
    private static Java2DFrameConverter biConv = new Java2DFrameConverter();


    public static BufferedImage deepCopy(BufferedImage source) {
        return Java2DFrameConverter.cloneBufferedImage(source);
    }

    public synchronized static opencv_core.Mat bufferedImageToJavacv(BufferedImage src) {
        return matConv.convertToMat(biConv.convert(src)).clone();
    }

    public synchronized static BufferedImage javacvToBufferedImage(opencv_core.Mat src) {
        return deepCopy(biConv.getBufferedImage(matConv.convert(src).clone()));
    }


    public static opencv_core.Mat pictureRemove(opencv_core.Mat src) {
        BufferedImage img = javacvToBufferedImage(src);
        //获取图片的高宽
        int width = img.getWidth();
        int height = img.getHeight();

        //循环执行除去干扰像素
        for (int i = 1; i < width; i++) {
            Color colorFirst = new Color(img.getRGB(i, 1));
            int numFirstGet = colorFirst.getRed() + colorFirst.getGreen() + colorFirst.getBlue();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color color = new Color(img.getRGB(x, y));
//                    System.out.println("red:" + color.getRed() + " | green:" + color.getGreen() + " | blue:" + color.getBlue());
                    int num = color.getRed() + color.getGreen() + color.getBlue();
                    if (num >= numFirstGet) {
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
        }

        //图片背景变黑色
        for (int i = 1; i < width; i++) {
            Color color1 = new Color(img.getRGB(i, 1));
            int num1 = color1.getRed() + color1.getGreen() + color1.getBlue();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color color = new Color(img.getRGB(x, y));
//                    System.out.println("red:" + color.getRed() + " | green:" + color.getGreen() + " | blue:" + color.getBlue());
                    int num = color.getRed() + color.getGreen() + color.getBlue();
                    if (num == num1) {
                        img.setRGB(x, y, Color.BLACK.getRGB());
                    } else {
                        img.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
        }
        return bufferedImageToJavacv(img);
    }


}