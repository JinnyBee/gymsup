package com.fitness.gymsup.Util;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class FileUploader {
    //파일 저장
    public String uploadFile(String uploadPth,
                             String originalFileName,
                             byte[] filedata) throws Exception {
        UUID uuid = UUID.randomUUID();
        String extendsion = originalFileName.substring(
                originalFileName.lastIndexOf(".")
        );
        String saveFileName = uuid.toString() + extendsion; //새로운 파일명
        String uploadFullUrl = uploadPth + saveFileName;    //파일 저장 위치

        FileOutputStream fos = new FileOutputStream(uploadFullUrl);
        fos.write(filedata);
        fos.close();

        return saveFileName;    //데이터베이스에 저장할 파일명 전달
    }

    //파일 삭제
    public void deleteFile(String uploadPath,
                           String fileName) throws Exception {
        String deleteFileName = uploadPath + fileName;
        File deleteFile = new File(deleteFileName);
        if(deleteFile.exists()) {
            deleteFile.delete();
        }
    }
}
