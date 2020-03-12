package com.hss.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UploadFileController {

    // 项目根路径下的目录  -- SpringBoot static 目录相当于是根路径下（SpringBoot 默认）
    @Value("${spring.servlet.multipart.location}")
    private String location;

    @RequestMapping("/file")
    @ResponseBody
    public List<String> file(@RequestParam(value = "img") MultipartFile[] files, HttpServletRequest request) {
        /*创建集合对象，用来存放上传后生成文件名*/
        List<String> fileNames = new ArrayList<>();
        String path = location;
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            /*生成文件名*/
            StringBuilder filePath = new StringBuilder(path);
            filePath.append(UUID.randomUUID().toString());
            String fileName = file.getOriginalFilename();
            filePath.append(fileName.substring(fileName.lastIndexOf(".")));

            File localFile = new File(filePath.toString());

            if (!localFile.getParentFile().exists()){
                localFile.mkdirs();
            }
            try {
                file.transferTo(localFile);
                /*上传成功，将文件名添加到集合中*/
                fileNames.add(localFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        返回集合
        return fileNames;
    }
}
