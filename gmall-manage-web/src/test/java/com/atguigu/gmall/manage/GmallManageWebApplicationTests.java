package com.atguigu.gmall.manage;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

//    @Test
//    void contextLoads() {
//    }

    @Test
    public void testFileUpload() throws IOException, MyException {
//        String file = this.getClass().getResource("/tracker.conf").getFile();
        String file = this.getClass().getResource("/tracker.conf").getPath();
        ClientGlobal.init(file);
        TrackerClient trackerClient = new TrackerClient();
        //h获得trackerserver实例
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer,null);
        String tp = "f:/1.jpg";
        String[] upload_file = storageClient.upload_file(tp,"jpg",null);
        for (int i = 0; i < upload_file.length; i++) {
            String s = upload_file[i];
            System.out.println(s);
        }
    }
}
