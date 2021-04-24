package com.topicmanager.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderinfoSimpleReport {
    private String id;
    private String thesisName;
    private String thesisCollege;    //所属学院
    private String student;           //所属学生
    private String reportDoc;
    private Boolean reportStatus;
}
