package com.luohuasheng.controller;

import com.luohuasheng.utils.DateUtils;
import com.luohuasheng.utils.WaterMarkUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

@Controller
public class FileController {

    private String imageFilePath;

    @PostConstruct
    public void init() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        imageFilePath = path.getPath() + "/";

    }


    @PostMapping("/dft")
    @ApiOperation(value = "添加水印", produces = "application/octet-stream")
    public void dft(HttpServletResponse response, @RequestParam("image") MultipartFile image, String msg) throws IOException {
        WaterMarkUtils bwm = new WaterMarkUtils();
        String filePath = String.format("%s%s", imageFilePath, DateUtils.format(new Date(), "yyyy/MM/dd/HH/"));
        String originalFileName = image.getOriginalFilename();
        String sourceFilePath = filePath + "/1/" + originalFileName;
        String targetFilePath = filePath + "/2/" + originalFileName;
        image.transferTo(checkFilePath(sourceFilePath));
        File file = checkFilePath(targetFilePath);
        bwm.encode(sourceFilePath, msg, targetFilePath, true);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(image.getOriginalFilename().getBytes("UTF-8"), "ISO-8859-1") + "\";");
        ServletOutputStream out = response.getOutputStream();
        StreamUtils.copy(new FileInputStream(file), response.getOutputStream());
        out.close();
    }

    private File checkFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;

    }

    @PostMapping("/idft")
    @ApiOperation(value = "获取水印", produces = "application/octet-stream")
    public void idft(HttpServletResponse response, @RequestParam("image") MultipartFile image) throws IOException {
        WaterMarkUtils bwm = new WaterMarkUtils();
        String filePath = String.format("%s%s", imageFilePath, DateUtils.format(new Date(), "yyyy/MM/dd/HH/"));
        String originalFileName = image.getOriginalFilename();
        String sourceFilePath = filePath + "/3/" + originalFileName;
        String targetFilePath = filePath + "/4/" + originalFileName;
        image.transferTo(checkFilePath(sourceFilePath));
        File file = checkFilePath(targetFilePath);
        bwm.decode(sourceFilePath, targetFilePath);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(image.getOriginalFilename().getBytes("UTF-8"), "ISO-8859-1") + "\";");
        StreamUtils.copy(new FileInputStream(file), response.getOutputStream());
    }

}
