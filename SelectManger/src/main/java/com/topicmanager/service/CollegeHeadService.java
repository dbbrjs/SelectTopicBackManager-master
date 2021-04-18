package com.topicmanager.service;

import com.topicmanager.mapper.CollegeHeadMapper;
import com.topicmanager.pojo.CollegeHead;
import com.topicmanager.utils.IDgenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollegeHeadService {

    @Autowired
    private CollegeHeadMapper collegeHeadMapper;

    public CollegeHead login(String loginName){
        CollegeHead collegeHead = collegeHeadMapper.login(loginName);
        return collegeHead;
    }


    public Integer updateInfo(CollegeHead collegeHead){
        return collegeHeadMapper.updateByPrimaryKeySelective(collegeHead);
    }
    public CollegeHead getInfo(String headId){
        return collegeHeadMapper.selectByPrimaryKey(headId);
    }
    public Integer insert(CollegeHead collegeHead){
        collegeHead.setCollegeId(IDgenerator.generatorHeadId());
        return collegeHeadMapper.insert(collegeHead);
    }

}
