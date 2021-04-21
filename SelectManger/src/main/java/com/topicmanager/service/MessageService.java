package com.topicmanager.service;

import com.topicmanager.enums.MessageTypeEnum;
import com.topicmanager.mapper.MessageMapper;
import com.topicmanager.mapper.OrderInfoMapper;
import com.topicmanager.mapper.StudentMapper;
import com.topicmanager.mapper.TeacherMapper;
import com.topicmanager.pojo.Message;
import com.topicmanager.pojo.Orderinfo;
import com.topicmanager.pojo.Student;
import com.topicmanager.pojo.Teacher;
import com.topicmanager.utils.IDgenerator;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    public void createMessage(String stId,String news){
        Orderinfo o = new Orderinfo();
        o.setStuNum(stId);
        Orderinfo orderinfo = orderInfoMapper.selectOne(o);
        Teacher teacher = new Teacher();
        teacher.setTeacherName(orderinfo.getTeacher());
        Teacher teacher1 = teacherMapper.selectOne(teacher);
        Message message = new Message();
        message.setMessageId(IDgenerator.generatorMessageId());
        message.setStudentId(stId);
        message.setTeacherId(teacher1.getTeacherId());
        message.setNews(news);
        message.setCreateTime(new Timestamp(System.currentTimeMillis()));
        message.setDirect(MessageTypeEnum.TOTEACHER.getDirect());
        messageMapper.insert(message);
    }

    public void createMessage(String teId,String stName,String news){
        Student student = new Student();
        student.setStudentName(stName);
        Student student1 = studentMapper.selectOne(student);
        Message message = new Message();
        message.setMessageId(IDgenerator.generatorMessageId());
        message.setStudentId(student1.getStudentId());
        message.setTeacherId(teId);
        message.setNews(news);
        message.setCreateTime(new Timestamp(System.currentTimeMillis()));
        message.setDirect(MessageTypeEnum.TOSTUDENT.getDirect());
        messageMapper.insert(message);
    }
    public List<Message> getByteId(String teId,String stName){
        Student student = new Student();
        student.setStudentName(stName);
        Student student1 = studentMapper.selectOne(student);
        Example example = new Example(Message.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("teacherId",teId);
        criteria.andEqualTo("studentId",student1.getStudentId());
        example.setOrderByClause("create_time asc");
        List<Message> messages = messageMapper.selectByExample(example);
        return messages;
    }

    public List<Message> getBystId(String stId){
        Example example = new Example(Message.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId",stId);
        example.setOrderByClause("create_time asc");
        List<Message> messages = messageMapper.selectByExample(example);
        return messages;
    }
}
