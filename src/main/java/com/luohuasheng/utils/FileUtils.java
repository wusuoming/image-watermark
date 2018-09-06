package com.luohuasheng.utils;

import org.springframework.util.StringUtils;

import java.io.File;

public class FileUtils {
    public static File checkFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;

    }

    public static String getSaveFileName(String originalFileName) {
        if (StringUtils.isEmpty(originalFileName)) {
            throw new RuntimeException("文件为空");
        }
        String[] originalFileNames = originalFileName.split("\\.");
        if (originalFileNames.length > 1) {
            return IdUtil.getId().toString() + "." + originalFileNames[originalFileNames.length - 1];
        } else {
            return IdUtil.getId().toString() + ".jpg";
        }
    }
}
