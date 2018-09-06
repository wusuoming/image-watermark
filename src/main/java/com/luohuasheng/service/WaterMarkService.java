package com.luohuasheng.service;

import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.opencv_imgproc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.putText;

/**
 * @author wusuoming
 */
@Service
public class WaterMarkService {

    private ThreadLocal<Integer> localLevel = new ThreadLocal<>();
    private ThreadLocal<List<Integer>> localSort = new ThreadLocal<>();


    public void encode(String image, String watermark, String output, Boolean text, Integer level) {
        localLevel.set(level);
        MatVector newPlanes = new MatVector(3);
        Mat srcImg = imread(image, CV_LOAD_IMAGE_COLOR);
        setLocalSorts(srcImg);
        List<Integer> sorts = localSort.get();
        MatVector color = new MatVector(3);
        split(srcImg, color);
        MatVector[] planes = {new MatVector(2), new MatVector(2), new MatVector(2)};
        for (int i = 0; i < color.size(); i++) {
            color.get(i).convertTo(color.get(i), CV_32F);
            Mat comImg = startDFT(color.get(i));
            if (level == 1) {
                if (i == sorts.get(0)) {
                    if (text) {
                        addTextWaterMark(comImg, watermark);
                    } else {
                        addImageWaterMark(comImg, watermark);
                    }
                }
            } else if (level == 2) {
                if (i == sorts.get(0) || i == sorts.get(1)) {
                    if (text) {
                        addTextWaterMark(comImg, watermark);
                    } else {
                        addImageWaterMark(comImg, watermark);
                    }
                }
            } else if (level == 3) {

                if (text) {
                    addTextWaterMark(comImg, watermark);
                } else {
                    addImageWaterMark(comImg, watermark);
                }

            }

            inverseDFT(comImg, planes[i]);
            newPlanes.put(i, comImg);
        }
        Mat nImg = new Mat();
        merge(newPlanes, nImg);
        imwrite(output, nImg);
        localLevel.remove();
        localSort.remove();
    }

    public void setLocalSorts(Mat srcImg) {
        UByteIndexer indexer = srcImg.createIndexer();
        List<PixelSum> pixelSums = new ArrayList<>();
        List<Integer> sorts = new ArrayList<>();
        //图像通道
        int nbChannels = srcImg.channels();
        for (int channel = 0; channel < nbChannels; channel++) {
            int channelSum = 0;
            for (int i = 0; i < srcImg.rows(); i++) {
                for (int j = 0; j < srcImg.cols(); j++) {
                    channelSum += (indexer.get(i, j, channel));
                }
            }
            PixelSum pixelSum = new PixelSum();
            pixelSum.setIndex(channel);
            pixelSum.setSum(channelSum);
            pixelSums.add(pixelSum);
        }
        pixelSums.sort(Comparator.comparing(PixelSum::getSum));
        for (PixelSum pixelSum : pixelSums) {
            sorts.add(pixelSum.getIndex());
        }
        localSort.set(sorts);
    }

    /**
     * 文本水印解码
     *
     * @param wmImg  加了文本水印的图像
     * @param output 图像中文本水印
     */
    public void decode(String wmImg, String output) {

        MatVector newPlanes = new MatVector(3);

        for (int i = 0; i < 3; i++) {
            Mat srcImg = imread(wmImg, i == 2 ? CV_LOAD_IMAGE_GRAYSCALE : CV_LOAD_IMAGE_COLOR);
            if (localSort.get() == null) {
                setLocalSorts(srcImg);
            }
            localLevel.set(i + 1);
            newPlanes.put(i, transformImage(srcImg));
        }
        Mat nImg2 = new Mat();
        Mat nImg = new Mat();
        vconcat(newPlanes.get(0), newPlanes.get(1), nImg);
        vconcat(nImg, newPlanes.get(2), nImg2);
//        merge(newPlanes,nImg2);
        imwrite(output, nImg2);
        localLevel.remove();
    }


    private Mat transformImage2(Mat decImg) {
        decImg.convertTo(decImg, CV_32F);
        decImg = startDFT(decImg);
        MatVector newPlanes = new MatVector(2);
        Mat mag = new Mat();
        split(decImg, newPlanes);
        magnitude(newPlanes.get(0), newPlanes.get(1), mag);
        add(Mat.ones(mag.size(), CV_32F).asMat(), mag, mag);
        log(mag, mag);
        shiftDFT(mag);
        mag.convertTo(mag, CV_8UC1);
        normalize(mag, mag, 0, 255, NORM_MINMAX, CV_8UC1, null);
        return mag;
    }


    /**
     * 图片水印解码
     *
     * @param srcImg 原图
     * @param wmImg  加了图片水印的图像
     * @param output 图像中的水印
     */
    public void decode(String srcImg, String wmImg, String output) {
        Mat decImg = imread(srcImg, CV_LOAD_IMAGE_GRAYSCALE);
        Mat wm = imread(wmImg, CV_LOAD_IMAGE_GRAYSCALE);

        decImg.convertTo(decImg, CV_32F);
        wm.convertTo(wm, CV_32F);


        //srcImg -= wmImg
        subtract(wm, startDFT(decImg), startDFT(wm));

        MatVector newPlanes = new MatVector(2);
        split(wm, newPlanes);
        wm = newPlanes.get(0);

        imwrite(output, wm);
    }

    /**
     * 将图像进行DFT
     *
     * @param srcImg 源图像
     * @return 转化后的图像
     */
    private static Mat startDFT(Mat srcImg) {
        MatVector planes = new MatVector(2);
        Mat comImg = new Mat();
        planes.put(0, srcImg);
        planes.put(1, Mat.zeros(srcImg.size(), CV_32F).asMat());
        merge(planes, comImg);
        dft(comImg, comImg);
        return comImg;
    }

    /**
     * DFT逆变换
     *
     * @param comImg DFT后的图像
     * @param planes 图像变量
     */
    private static void inverseDFT(Mat comImg, MatVector planes) {
        idft(comImg, comImg);
        split(comImg, planes);
        normalize(planes.get(0), comImg, 0, 255, NORM_MINMAX, CV_8UC3, null);
    }


    /**
     * 交换频谱图四个象限
     *
     * @param comImg 频谱图
     */
    private static void shiftDFT(Mat comImg) {
        comImg = new Mat(comImg, new Rect(0, 0, comImg.cols() & -2, comImg.rows() & -2));
        int cx = comImg.cols() / 2;
        int cy = comImg.rows() / 2;

        Mat q0 = new Mat(comImg, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(comImg, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(comImg, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(comImg, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    /**
     * 添加文本水印
     *
     * @param comImg 频谱图
     */
    private static void addTextWaterMark(Mat comImg, String watermark) {

        Scalar scalar = new Scalar(255, 255, 255, 255);
        Point p = new Point(40, 40);
        // 添加字符串
        putText(comImg, watermark, p, opencv_imgproc.CV_FONT_HERSHEY_DUPLEX, 1, scalar, 2, LINE_8, false);
        // 旋转图片
        flip(comImg, comImg, -1);
        putText(comImg, watermark, p, opencv_imgproc.CV_FONT_HERSHEY_DUPLEX, 1, scalar, 2, LINE_8, false);
        flip(comImg, comImg, -1);
    }

    /**
     * 添加图片水印
     *
     * @param comImg 频谱图
     */
    private static void addImageWaterMark(Mat comImg, String watermark) {
        Mat wm = imread(watermark, CV_LOAD_IMAGE_GRAYSCALE);
        MatVector planes = new MatVector(2);
        wm.convertTo(wm, CV_32F);
        //same size
        createWaterMark(comImg, wm);
        //水印编码
        //...

        //same channel
        planes.put(0, wm);
        planes.put(1, wm);
        merge(planes, wm);

        //add mark
        addWeighted(wm, 0.5, comImg, 1, 0.0, comImg);
    }

    /**
     * 获取相同大小图层
     *
     * @param comImg 原图
     * @param wm     需改变的图像
     */
    private static void createWaterMark(Mat comImg, Mat wm) {
        MatVector combine = new MatVector(2);
        Mat iwm = new Mat();
        copyMakeBorder(wm, wm, 0, comImg.rows() / 2 - wm.rows(),
                0, comImg.cols() - wm.cols(), BORDER_CONSTANT, Scalar.all(0));
        combine.put(0, wm);
        flip(wm, iwm, -1);
        combine.put(1, iwm);
        vconcat(combine, wm);
    }

    public Mat transformImage(Mat image) {
        MatVector planes = new MatVector(2);
        Mat complexImage = new Mat();
        Integer level = localLevel.get();
        List<Integer> sorts = localSort.get();

        if (level == 1) {
            Mat padded = splitSrc(image, sorts.get(0));
            padded.convertTo(padded, CV_32F);
            planes.put(0, padded);
            planes.put(1, padded);
        } else if (level == 2) {
            for (int i = 0; i < 2; i++) {
                Mat padded = splitSrc(image, sorts.get(i));
                padded.convertTo(padded, CV_32F);
                planes.put(i, padded);
            }
        } else {
            return transformImage2(image);
        }
        merge(planes, complexImage);
        dft(complexImage, complexImage);
        Mat magnitude = createOptimizedMagnitude(complexImage);
        planes.clear();
        return magnitude;
    }

    private Mat createOptimizedMagnitude(Mat complexImage) {
        MatVector color = new MatVector(3);
        Mat mag = new Mat();
        split(complexImage, color);
        magnitude(color.get(0), color.get(1), mag);
        add(Mat.ones(mag.size(), CV_32F).asMat(), mag, mag);
        log(mag, mag);
        shiftDFT(mag);
        mag.convertTo(mag, CV_8UC1);
        normalize(mag, mag, 0, 255, NORM_MINMAX, CV_8UC1, null);
        return mag;
    }


    /**
     * 根据下标获取指定色域图层
     *
     * @param mat   源图像
     * @param index 指定下标
     * @return 所获取的图层
     */
    private Mat splitSrc(Mat mat, Integer index) {

        MatVector color = new MatVector(3);
        split(mat, color);

        if (color.size() > 1) {
            return color.get(index);
        } else {
            return mat;
        }
    }

    private class PixelSum {
        private Integer index;
        private Integer sum;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Integer getSum() {
            return sum;
        }

        public void setSum(Integer sum) {
            this.sum = sum;
        }
    }

}