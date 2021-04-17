package com.topicmanager.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.topicmanager.mapper.CollegeHeadMapper;
import com.topicmanager.mapper.StudentMapper;
import com.topicmanager.mapper.TeacherMapper;
import com.topicmanager.pojo.*;
import com.topicmanager.result.ListResult;
import com.topicmanager.utils.IDgenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private CollegeHeadMapper collegeHeadMapper;

    public Teacher login(String loginName){
        Teacher teacher = teacherMapper.login(loginName);
        return teacher;
    }


    //修改教师信息
    public Integer editTeacherInfo(Teacher teacher) {
        return teacherMapper.updateByPrimaryKey(teacher);
    }

    //select teacher by id
    public Teacher getTeacherById(String teacherId) {
        return teacherMapper.selectByPrimaryKey(teacherId);
    }


    public List<StudentThesis> getStudent(String teacherName) {
        List<StudentThesis> studentOfTeachers = studentMapper.getStudentOfTeacher(teacherName);
        return studentOfTeachers;
    }

    public Teacher getTeacherByName(String teacherName) {
        Teacher teacher = new Teacher();
        teacher.setTeacherName(teacherName);
        return  teacherMapper.selectOne(teacher);
    }

    public ListResult getTeacherByCollegeHead(int pageNum, int pageSize, String headId,String teName){
        CollegeHead collegeHead = collegeHeadMapper.selectByPrimaryKey(headId);
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Teacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("college", collegeHead.getCollege());
        if (teName!=null){
            criteria.andLike("teacherName", "%"+teName+"%");
        }
        List<Teacher> teachers = teacherMapper.selectByExample(example);
        int count = teacherMapper.selectCountByExample(example);
        PageInfo<Teacher> info = new PageInfo<>(teachers);
        ListResult teacherResult = new ListResult(info.getList(),count);
        return teacherResult;
    }

    public Integer addTeacher(Teacher t) {
        t.setTeacherId(IDgenerator.generatorTeaId());
        return teacherMapper.insert(t);
    }
}
