package com.topicmanager.enums;

public enum  ThesisStatusEnum {
    PENDING(1),
    SUCESS(2),
    REJECT(3)
    ;
    private int status;

    ThesisStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

