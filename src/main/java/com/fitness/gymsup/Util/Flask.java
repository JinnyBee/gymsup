package com.fitness.gymsup.Util;

import com.fitness.gymsup.DTO.FlaskResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class Flask {
    //S3 업로드
    private final S3Uploader s3Uploader;
    //플라스크 서버로부터 응답 온 결과 이미지 파일이 저장된 S3 업로드 경로
    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    //Flask 클래스 호출시 반드시 Autowired로 선언해야 한다. 자동주입시 Value가 인식 안된다.
    @Value("${flask.Server.Url}")
    private String url;

    @Value("${tempFolder}")
    private String tempFolder;

    private JSONObject jsonobject = new JSONObject();

    private String getBase64String(MultipartFile multipartFile) throws Exception {
        byte[] bytes = multipartFile.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public FlaskResponseDTO requestToFlask(MultipartFile file) throws Exception {
        //다른 서버와 통신을 위한 RestTemplate 선언
        RestTemplate restTemplate = new RestTemplate();
        String originalFileName = file.getOriginalFilename(); //파일명 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); //확장자 추출

        //메세지 헤더부분 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED); //(MediaType.APPLICATION_JSON);

        //메세지 본문부분 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        String imageFileString = getBase64String(file);

        //body.add("filename", fileName);  파일명으로 전송시
        body.add("extension", extension); //확장자만 전송시
        body.add("image", imageFileString);

        //전송할 메세지 만들기
        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);
        String response = "";
        log.info("REQ : " + requestMessage.toString());


        try {
            //서버에 요청하기
            //ResponseEntity<String> response = restTemplate.postForEntity(url, requestMessage, String.class);
            response = restTemplate.postForObject(url, requestMessage, String.class);

            log.info("RES : " + response);
        } catch (HttpStatusCodeException e) {
            String errorload = e.getResponseBodyAsString();
            log.error(e.getStatusCode() + " : " + errorload);

            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        //플라스크로 부터 받은 JSON값을 분리
        JSONParser parser = new JSONParser();
        jsonobject = (JSONObject) parser.parse(response);

        //JSON으로 전달받은 값 개수확인
        System.out.println(jsonobject.size());

        //플라스크에서 전달받은 파일을 임시저장
        byte[] decodedImageDate = Base64.getDecoder().decode((String)(jsonobject.get("image")));
        String outputFilePath = tempFolder + UUID.randomUUID() + "_result.jpg"; //AI결과 임시저장 파일명
        log.info("AI result temp filename : " + outputFilePath);

        try(FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decodedImageDate);
        }
        String newFileName = s3Uploader.upload(new File(outputFilePath), imgUploadLocation);
        log.info("s3Uploader newFileName : " + newFileName);

        //분류내용 처리
        //JSONObject jo =(JSONObject)jsonobject.get("class");
        JSONArray jsonArray = (JSONArray) jsonobject.get("class");
        FlaskResponseDTO dto = new FlaskResponseDTO();
        //dto.setResultFilename(outputFilePath);
        //dto.setResultFilename("result.jpg");
        dto.setResultFilename(newFileName);
        dto.setName(jsonArray);

        //분리 테스트
        for(String name:dto.getName()) {
            System.out.println(name);
        }

        return dto;

        //for(Object name:jsonArray) {
        //    System.out.println(name);
        //}
        //FlaskResponseDTO dto = new FlaskResponseDTO();
        //dto.setName(jsonobject.get("class"));
        //S3에 파일 전송
        //File dataFile = new File(outputFilePath);
        //s3Uploader.AIResultS3(dataFile, imgUploadLocation+"/result.jpg");

        //임시파일삭제
        //File deleteFile = new File(outputFilePath);
        //if(deleteFile.exists()) { //파일이 존재하면
        //    deleteFile.delete();
        //}
    }
}