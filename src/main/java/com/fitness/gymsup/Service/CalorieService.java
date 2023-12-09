/*
    파일명 : CalorieService.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.ExerciseDTO;
import com.fitness.gymsup.DTO.FoodCalorieDTO;
import com.fitness.gymsup.Entity.ExerciseEntity;
import com.fitness.gymsup.Repository.BoardRepository;
import com.fitness.gymsup.Repository.BookmarkRepository;
import com.fitness.gymsup.Repository.ExerciseRepository;
import com.fitness.gymsup.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CalorieService {
    //주입 : Repository, ModelMapper
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ExerciseRepository exerciseRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Value("${fooddb.Server.Url}")
    private String fooddbServerUrl;

    public void myBMICalculate(Integer userId, Integer boardId) throws Exception{
        //bookmark 테이블에서 해당 북마크 삭제
        //bookmarkRepository.deleteAllByUserIdAndBoardId(userId, boardId);
    }

    public List<FoodCalorieDTO> requestToFoodDB(String keyword) throws Exception {
        StringBuffer response = new StringBuffer();

        try {
            String requestUrl = fooddbServerUrl + "DESC_KOR=" + keyword;

            log.info("REQ : " + requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(30000);
            urlConnection.setUseCaches(false);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String returnLine;

            while((returnLine = br.readLine()) != null) {
                log.info(returnLine);
                response.append(returnLine);
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            log.info(e.toString());
        }
        log.info("RES : " + response.toString());

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
        JSONObject i2790Object = (JSONObject) jsonObject.get("I2790");
        JSONArray rowArray = (JSONArray) i2790Object.get("row");

        //데이터를 저장할 DTO 리스트
        List<FoodCalorieDTO> foodCalorieDTOS = new ArrayList<>();
        for(Object obj : rowArray) {
            JSONObject rowObject = (JSONObject) obj;
            FoodCalorieDTO foodCalorieDTO = new FoodCalorieDTO();

            foodCalorieDTO.setGroupName((String) rowObject.get("GROUP_NAME"));
            foodCalorieDTO.setDescKor((String) rowObject.get("DESC_KOR"));

            foodCalorieDTO.setResearchYear((String) rowObject.get("RESEARCH_YEAR"));
            foodCalorieDTO.setMakerName((String) rowObject.get("MAKER_NAME"));
            foodCalorieDTO.setSubRefName((String) rowObject.get("SUB_REF_NAME"));

            foodCalorieDTO.setServingSize((String) rowObject.get("SERVING_SIZE"));
            foodCalorieDTO.setServingUnit((String) rowObject.get("SERVING_UNIT"));

            foodCalorieDTO.setNutrCont1((String) rowObject.get("NUTR_CONT1"));
            foodCalorieDTO.setNutrCont2((String) rowObject.get("NUTR_CONT2"));
            foodCalorieDTO.setNutrCont3((String) rowObject.get("NUTR_CONT3"));
            foodCalorieDTO.setNutrCont4((String) rowObject.get("NUTR_CONT4"));
            foodCalorieDTO.setNutrCont5((String) rowObject.get("NUTR_CONT5"));
            foodCalorieDTO.setNutrCont6((String) rowObject.get("NUTR_CONT6"));
            foodCalorieDTO.setNutrCont7((String) rowObject.get("NUTR_CONT7"));
            foodCalorieDTO.setNutrCont8((String) rowObject.get("NUTR_CONT8"));
            foodCalorieDTO.setNutrCont9((String) rowObject.get("NUTR_CONT9"));

            foodCalorieDTOS.add(foodCalorieDTO);
        }
        log.info(foodCalorieDTOS);

        return foodCalorieDTOS;
    }

    public Page<ExerciseDTO> list(int page,
                                  String keyword) throws Exception {
        int pageLimit = 10;
        Page<ExerciseEntity> exerciseEntitie;
        Pageable pageable = PageRequest.of(page-1, pageLimit,Sort.by(Sort.Direction.DESC, "id"));
        //검색조건에 따른 조회
        if (keyword != null) {
            exerciseEntitie = exerciseRepository.searchExercisename(pageable, keyword);
        }else {
            exerciseEntitie = exerciseRepository.findAll(pageable);
        }

        // 결과변환
        Page<ExerciseDTO> exerciseDTOS = exerciseEntitie.map(data->ExerciseDTO.builder()
                .id(data.getId())
                .exercisename(data.getExercisename())
                .kcal(data.getKcal())
                .minute(data.getMinute())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build()
        );

        return exerciseDTOS;
    }
}