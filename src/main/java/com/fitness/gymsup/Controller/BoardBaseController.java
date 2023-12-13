/*
    파일명 : BoardBaseController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 : 전현진
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;

public abstract class BoardBaseController {

    public String getDetailRedirectUrl(String categoryType) {

        String redirectUrl = "";
        if (categoryType.equals(BoardCategoryType.BTYPE_NOTIFY.name())) {
            redirectUrl = "/board_notify_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_TIP.name())) {
            redirectUrl = "/board_tip_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_DIARY.name())) {
            redirectUrl = "/board_diary_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_QNA.name())) {
            redirectUrl = "/board_qna_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_FREE.name())) {
            redirectUrl = "/board_free_detail";
        } else {
            redirectUrl = "/";
        }

        return redirectUrl;
    }

    public String getReloadRedirectUrl(String categoryType) {

        String redirectUrl = "";
        if (categoryType.equals(BoardCategoryType.BTYPE_NOTIFY.name())) {
            redirectUrl = "/board_notify_reload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_TIP.name())) {
            redirectUrl = "/board_tip_reload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_DIARY.name())) {
            redirectUrl = "/board_diary_reload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_QNA.name())) {
            redirectUrl = "/board_qna_reload";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_FREE.name())) {
            redirectUrl = "/board_free_reload";
        } else {
            redirectUrl = "/";
        }

        return redirectUrl;
    }
}
