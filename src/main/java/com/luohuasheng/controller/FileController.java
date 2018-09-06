package com.luohuasheng.controller;

import com.luohuasheng.service.WaterMarkService;
import com.luohuasheng.utils.DateUtils;
import com.luohuasheng.utils.FileUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author wusuoming
 */
@Controller
public class FileController {

    @Autowired
    private WaterMarkService waterMarkService;

    private String imageFilePath;


    @PostConstruct
    public void init() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("").getPath());
        if (!path.exists()) {
            path = new File("");
        }
        imageFilePath = path.getAbsolutePath() + "/";


    }


    @PostMapping("/encode")
    @ApiOperation(value = "添加水印", produces = "application/octet-stream")
    public void encode(HttpServletResponse response, @RequestParam("image") MultipartFile image, @RequestParam("msg") String msg, @RequestParam(value = "level", defaultValue = "1", required = false) Integer level) throws IOException {
        String filePath = String.format("%s%s", imageFilePath, DateUtils.format(new Date(), "yyyy/MM/dd/HH/"));
        String originalFileName = image.getOriginalFilename();
        String sourceFilePath = filePath + "/1/" + FileUtils.getSaveFileName(originalFileName);
        String targetFilePath = filePath + "/2/" + FileUtils.getSaveFileName(originalFileName);
        image.transferTo(FileUtils.checkFilePath(sourceFilePath));
        File file = FileUtils.checkFilePath(targetFilePath);
        waterMarkService.encode(sourceFilePath, msg, targetFilePath, true, level);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1") + "\";");
        ServletOutputStream out = response.getOutputStream();
        StreamUtils.copy(new FileInputStream(file), response.getOutputStream());
        out.close();
    }


    @PostMapping("/decode")
    @ApiOperation(value = "获取水印", produces = "application/octet-stream")
    public void decode(HttpServletResponse response, @RequestParam("image") MultipartFile image, @RequestParam(value = "level", defaultValue = "1", required = false) Integer level) throws IOException {
        String filePath = String.format("%s%s", imageFilePath, DateUtils.format(new Date(), "yyyy/MM/dd/HH/"));
        String originalFileName = image.getOriginalFilename();
        String sourceFilePath = filePath + "/3/" + FileUtils.getSaveFileName(originalFileName);
        String targetFilePath = filePath + "/4/" + FileUtils.getSaveFileName(originalFileName);
        image.transferTo(FileUtils.checkFilePath(sourceFilePath));
        File file = FileUtils.checkFilePath(targetFilePath);
        waterMarkService.decode(sourceFilePath, targetFilePath, level);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(image.getOriginalFilename().getBytes("UTF-8"), "ISO-8859-1") + "\";");
        StreamUtils.copy(new FileInputStream(file), response.getOutputStream());
    }


}