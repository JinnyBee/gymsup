package com.fitness.gymsup.DTO;

import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.LocalDateTime;
@Log4j2
public class Time {
    private static class TIME_MAXIMUM {
        public static final int SEC = 60;                           // 초
        public static final int MIN = 60;                           // 분
        public static final int HOUR = 24;                          // 시간
        public static final int DAY = 30;                           // 일
        public static final int MONTH = 12;                         // 월
    }
    public static String calculateTime(LocalDateTime date) {        // LocalDateTime 이용
        LocalDateTime curTime = LocalDateTime.now();                // LocalDateTime.now() 현재시간 가져오기
        Duration duration = Duration.between(date, curTime);        //
        long diffTime = duration.getSeconds();
        String msg = null;

        long seconds = diffTime % TIME_MAXIMUM.SEC;
        diffTime /= TIME_MAXIMUM.SEC;
        long minutes = diffTime % TIME_MAXIMUM.MIN;
        diffTime /= TIME_MAXIMUM.MIN;
        long hours = diffTime % TIME_MAXIMUM.HOUR;
        diffTime /= TIME_MAXIMUM.HOUR;
        long days = diffTime % TIME_MAXIMUM.DAY;
        diffTime /= TIME_MAXIMUM.DAY;
        long months = diffTime % TIME_MAXIMUM.MONTH;
        long years = diffTime / TIME_MAXIMUM.MONTH;

        if (years > 0) {
            msg = years + "년 전";
        } else if (months > 0) {
            msg = months + "달 전";
        } else if (days > 0) {
            msg = days + "일 전";
        } else if (hours > 0) {
            msg = hours + "시간 전";
        } else if (minutes > 0) {
            msg = minutes + "분 전";
        } else {
            msg = seconds + "초 전";
        }
        return msg;
    }
}
