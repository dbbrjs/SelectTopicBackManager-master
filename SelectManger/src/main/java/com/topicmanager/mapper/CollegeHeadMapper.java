package com.topicmanager.mapper;

import com.topicmanager.pojo.CollegeHead;
import com.topicmanager.pojo.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface CollegeHeadMapper extends Mapper<CollegeHead> {

    @Select("select * from college_head where login_name = #{ loginName }")
    CollegeHead login(@Param("loginName") String loginName);
}
