package com.topicmanager.pojo;

import lombok.Data;

import javax.persistence.Id;

@Data
public class CollegeHead {
    @Id
    private String headId;
    private String headName;
    private String loginName;
    private String headPwd;
    private String collegeId;
    private String college;
    private String sex;
    private String phone;
    private String email;
    private String headPost;

}
