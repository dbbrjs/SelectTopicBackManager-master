package com.topicmanager.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "student")
@NoArgsConstructor
public class Student {

    @Id
    private String studentId;
    private String loginName;
    private String studentName;
    private String studentPwd;
    private String sex;
    private String college;       //学院
    private String speciality;    //专业
    private String classNumber;   //班级
    private String phone;
    private String email;
    private Integer status;

}
