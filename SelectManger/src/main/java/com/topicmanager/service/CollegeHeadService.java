package com.topicmanager.service;

import com.topicmanager.mapper.CollegeHeadMapper;
import com.topicmanager.pojo.CollegeHead;
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

}
