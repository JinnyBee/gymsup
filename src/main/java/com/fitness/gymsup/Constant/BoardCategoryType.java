package com.fitness.gymsup.Constant;

public enum BoardCategoryType {
    BTYPE_ALL("전체 게시판"),
    BTYPE_NOTIFY("공지사항"),
    BTYPE_TIP("운동 팁"),
    BTYPE_DIARY("운동+식단 일기"),
    BTYPE_QNA("고민나눔"),
    BTYPE_FREE("자유 게시판");

    private String description;

    BoardCategoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}