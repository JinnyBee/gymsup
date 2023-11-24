package com.fitness.gymsup.Constant;

public enum BookmarkType {
    BOOKMARK("북마크"),
    LIKE("좋아요");

    private String description;

    BookmarkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}