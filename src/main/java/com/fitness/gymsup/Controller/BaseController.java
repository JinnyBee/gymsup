package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;

public abstract class BaseController {
    public String getRedirectUrl(String categoryType) {
        String redirectUrl = "";
        if (categoryType.equals(BoardCategoryType.BTYPE_NOTIFY.name())) {
            redirectUrl = "/board_notify_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_TIP.name())) {
            redirectUrl = "/board_tip_detailreload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_DIARY.name())) {
            redirectUrl = "/board_diary_detailreload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_QNA.name())) {
            redirectUrl = "/board_qna_detail";
        } else {
            redirectUrl = "/";
        }
        return redirectUrl;
    }
}
