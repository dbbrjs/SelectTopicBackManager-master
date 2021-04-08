package com.topicmanager.controller;

import com.topicmanager.pojo.CollegeHead;
import com.topicmanager.pojo.Thesis;
import com.topicmanager.result.CodeMsg;
import com.topicmanager.result.Result;
import com.topicmanager.service.CollegeHeadService;
import com.topicmanager.service.ThesisService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/head")
public class CollegeHeadController {
    @Autowired
    private CollegeHeadService collegeHeadService;
    @Autowired
    private ThesisService thesisService;

    @PostMapping("/login")
    public Result<CollegeHead> login(@Param("loginName") String loginName, @Param("headPwd") String headPwd){

        CollegeHead collegeHead = collegeHeadService.login(loginName);

        if (collegeHead == null) return Result.error(CodeMsg.USER_NOT_FOUND);

        if(!(collegeHead.getHeadPwd().equals(headPwd))) return Result.error(CodeMsg.PWD_WRONG);

        return  Result.success(collegeHead);
    }

    //获取学院管理员名下课题
    @GetMapping("/headThesis")
    @ResponseBody
    public Result<List<Thesis>> getThesisByTeacherName(@Param("headId") String headId){
        List<Thesis> thesislist = thesisService.getThesisByCollegeHead(headId);
        return  Result.success(thesislist);
    }
}
