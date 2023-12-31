package com.fitness.gymsup.Util;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {

        log.info(multipartFile.getOriginalFilename());
        log.info(dirName);
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));

        return upload(uploadFile, dirName);
    }

    // S3 파일삭제
    public void deleteFile(String deleteFile, String dirName) throws IOException {

        String fileName = dirName+"/"+deleteFile;
        log.info("bucket : " + bucket + ", deleteFile : " + fileName);

        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch(SdkClientException e) {
            e.printStackTrace();
            throw new IOException("Error deleting file from S3", e);
        }
    }

    // S3로 파일 업로드하기
    public String upload(File uploadFile, String dirName) {

        //난수명+파일명 => 새로운 파일명
        String newFileName = UUID.randomUUID() + uploadFile.getName();
        //String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        String fileName = dirName + "/" + newFileName;   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드

        log.info("fileName : " + fileName);
        removeNewFile(uploadFile);

        return newFileName;
        //return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {

        if (targetFile.delete()) {
            log.info("File delete success : " + targetFile);
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile multipartFile) throws IOException {

        //System.out.println(System.getProperty("user.dir"));
        log.info(System.getProperty("user.dir"));
        log.info(multipartFile.getOriginalFilename());

        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}