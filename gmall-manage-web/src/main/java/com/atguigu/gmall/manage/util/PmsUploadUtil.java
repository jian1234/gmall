package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile){
        // 图片服务器地址
        String imgUrl = "http://192.168.42.237";
        // 获取fdfs配置文件地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(tracker);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();
        // 获取trackerServer 实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通过tracker获取storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);
        try {
            byte[] bytes = multipartFile.getBytes();
            //获取文件后缀名
            String orgFilename = multipartFile.getOriginalFilename();
            int i = orgFilename.lastIndexOf(".");
            String extName = orgFilename.substring(i+1);
            String[] uploadInfo = storageClient.upload_file(bytes,extName,null);
            for (String uploadInfos : uploadInfo){
                imgUrl += "/"+uploadInfos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
