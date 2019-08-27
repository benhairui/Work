package com.ic.test;

public enum  ES_Enum {

    SEARCH_INDEX("ic"),
    SEARCH_TYPE("sen"),

    USER_INDEX("user"),
    USER_TYPE("info"),

    INSERT_INDEX("2"),
    INSERT_TYPE("3")

    ;

    private String name;

    private ES_Enum(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
