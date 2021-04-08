package com.topicmanager.controller;

import com.topicmanager.pojo.CollegeHead;
import com.topicmanager.pojo.Teacher;
import com.topicmanager.result.CodeMsg;
import com.topicmanager.result.Result;
import com.topicmanager.service.CollegeHeadService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/head")
public class CollegeHeadController {
    @Autowired
    private CollegeHeadService collegeHeadService;

    @PostMapping("/login")
    public Result<CollegeHead> login(@Param("loginName") String loginName, @Param("headPwd") String headPwd){

        CollegeHead collegeHead = collegeHeadService.login(loginName);

        if (collegeHead == null) return Result.error(CodeMsg.USER_NOT_FOUND);

        if(!(collegeHead.getHeadPwd().equals(headPwd))) return Result.error(CodeMsg.PWD_WRONG);

        return  Result.success(collegeHead);
    }
}
