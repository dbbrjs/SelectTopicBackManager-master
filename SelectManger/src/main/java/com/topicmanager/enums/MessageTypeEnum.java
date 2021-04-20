package com.topicmanager.enums;

public enum  MessageTypeEnum {
    TOSTUDENT(1),
    TOTEACHER(2)
    ;
    private int direct;

    MessageTypeEnum(int status) {
        this.direct = status;
    }
    public int getDirect() {
        return direct;
    }
}
