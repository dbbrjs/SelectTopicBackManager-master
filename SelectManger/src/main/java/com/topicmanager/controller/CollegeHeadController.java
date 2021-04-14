package com.topicmanager.controller;

import com.topicmanager.enums.ApplyThesisStatusEnum;
import com.topicmanager.pojo.CollegeHead;
import com.topicmanager.pojo.Thesis;
import com.topicmanager.result.CodeMsg;
import com.topicmanager.result.ListResult;
import com.topicmanager.result.Result;
import com.topicmanager.service.CollegeHeadService;
import com.topicmanager.service.StudentService;
import com.topicmanager.service.TeacherService;
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
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @PostMapping("/login")
    public Result<CollegeHead> login(@Param("loginName") String loginName, @Param("headPwd") String headPwd){

        CollegeHead collegeHead = collegeHeadService.login(loginName);

        if (collegeHead == null) return Result.error(CodeMsg.USER_NOT_FOUND);

        if(!(collegeHead.getHeadPwd().equals(headPwd))) return Result.error(CodeMsg.PWD_WRONG);

        return  Result.success(collegeHead);
    }

    //获取学院管理员名下课题
    @GetMapping("/headthesis")
    @ResponseBody
    public Result<List<Thesis>> getThesisByHeadName(@Param("headId") String headId){
        List<Thesis> thesislist = thesisService.getThesisByCollegeHead(headId);
        return  Result.success(thesislist);
    }
    //获取学院学生
    @GetMapping("/getstudent")
    @ResponseBody
    public Result<ListResult> getstudentByHeadId(@Param("pageNum")int pageNum,
                                                  @Param("pageSize") int pageSize, @Param("headId") String headId){
        ListResult result = studentService.getStudentByCollegeHead(pageNum,pageSize,headId);
        return  Result.success(result);
    }
    //获取学院管理员名下课题
    @GetMapping("/getteacher")
    @ResponseBody
    public Result<ListResult> getThesisByByHeadId(@Param("pageNum")int pageNum,
                                                  @Param("pageSize") int pageSize,@Param("headId") String headId){
        ListResult result = teacherService.getTeacherByCollegeHead(pageNum,pageSize,headId);
        return  Result.success(result);
    }

    @GetMapping("/pendingthesis")
    public Result<ListResult> getPendgingThesisByByHeadId(@Param("pageNum")int pageNum,
                                                  @Param("pageSize") int pageSize,@Param("headId") String headId){
        ListResult result = thesisService.getApplyThesisByCollegeHead(pageNum,pageSize,headId);
        return  Result.success(result);
    }
    @PostMapping("/confirm")
    public Result<Void> confirm(@Param("thesisId")String thesisId){
        thesisService.changeApplyThesisStatus(thesisId, ApplyThesisStatusEnum.FINISH.getStatus());
        return Result.success(null);
    }

    @PostMapping("/refuse")
    public Result<Void> refuse(@Param("thesisId")String thesisId){
        thesisService.changeApplyThesisStatus(thesisId, ApplyThesisStatusEnum.HEAD_REFUSE.getStatus());
        return Result.success(null);
    }
}
