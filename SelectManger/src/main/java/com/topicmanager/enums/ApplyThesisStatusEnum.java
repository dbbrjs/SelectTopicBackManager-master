package com.topicmanager.enums;

public enum ApplyThesisStatusEnum {
    WAIT_TEACHER_CHECK(1),
    TEACHER_REFUSE(2),
    WAIT_HEAD_CHECK(3),
    HEAD_REFUSE(4),
    FINISH(5),
    ;

    private Integer status;

   private ApplyThesisStatusEnum(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
