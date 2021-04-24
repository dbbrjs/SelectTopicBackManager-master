package com.topicmanager.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.topicmanager.mapper.CollegeHeadMapper;
import com.topicmanager.mapper.OrderInfoMapper;
import com.topicmanager.mapper.StudentMapper;
import com.topicmanager.mapper.ThesisMapper;
import com.topicmanager.pojo.*;
import com.topicmanager.result.ListResult;
import com.topicmanager.utils.IDgenerator;
import com.topicmanager.utils.ThesisStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ThesisMapper thesisMapper;
    @Autowired
    private CollegeHeadMapper collegeHeadMapper;

    //login
    public Student login(String loginName){
        Student s = new Student();
        s.setLoginName(loginName);
        Student student = studentMapper.selectOne(s);
//        System.out.println(student);
        return student;
    }


    public Student getStudent(String studentId) {
        Student s = new Student();
        s.setStudentId(studentId);
        Student student = studentMapper.selectByPrimaryKey(s);
        return student;
    }

    public Integer editStudent(Student s) {
        return studentMapper.updateByPrimaryKey(s);
    }

    public ListResult getStudentByCollegeHead(int pageNum,int pageSize,String headId,String stName){
        CollegeHead collegeHead = collegeHeadMapper.selectByPrimaryKey(headId);
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Student.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("college", collegeHead.getCollege());
        if (stName!=null){
            criteria.andLike("studentName", "%"+stName+"%");
        }
        List<Student> students = studentMapper.selectByExample(example);
        int count = studentMapper.selectCountByExample(example);
        PageInfo<Student> info = new PageInfo<>(students);
        ListResult studentResult = new ListResult(info.getList(),count);
        return studentResult;
    }

    public Integer chooseThesis(Thesis thesis, String studentId, String studentName) {
        Integer isOk = isChoose(studentId);
        if(isOk == 1){
            Orderinfo orderinfo = setOrderInfo(thesis, studentId, studentName);
//            System.out.println(orderinfo);
            if (thesis.getModel().equals("师生互选课题")){
                orderinfo.setStatus("待审核");
            }
            else
            {
                orderinfo.setStatus("审核通过");
            }
            thesisMapper.changeChooseToYES(thesis.getThesisId());
            return orderInfoMapper.insert(orderinfo);
        }else{
            return 0;   //选题失败
        }
    }


    @Transactional
    public Integer isChoose(String studentId){
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setStuNum(studentId);
//        System.out.println(orderinfo);
        Orderinfo o = orderInfoMapper.selectOne(orderinfo);
//        System.out.println(o);
        if (o == null){
            return 1;        //未选 = 可以选题
        }else if(o.getStatus().equals(ThesisStatus.REJECT)){
            orderInfoMapper.delete(o);     //审核未通过
            return 1;
        } else{
            return 0;        //不能选题
        }
    }


    public Integer addStudent(Student s) {
        s.setStudentId(IDgenerator.generatorStuId());
        return studentMapper.insert(s);
    }

    public Orderinfo setOrderInfo(Thesis thesis, String studentId, String studentName){
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setId(IDgenerator.generatorOrderId());
        orderinfo.setStuNum(studentId);
        orderinfo.setSisNum(thesis.getThesisId());
        orderinfo.setStatus(ThesisStatus.PENDING);
        orderinfo.setClassroom(thesis.getClassroom());
        orderinfo.setAllowSpecial(thesis.getAllowSpecial());
        orderinfo.setModel(thesis.getModel());
        orderinfo.setStudent(studentName);
        orderinfo.setTeacher(thesis.getTeacher());
        orderinfo.setThesisCollege(thesis.getThesisCollege());
        orderinfo.setThesisDate(thesis.getThesisDate());
        orderinfo.setThesisDoc(thesis.getThesisDoc());
        orderinfo.setThesisFrom(thesis.getThesisFrom());
        orderinfo.setThesisName(thesis.getThesisName());
        orderinfo.setThesisType(thesis.getThesisType());
        orderinfo.setThesisDesc(thesis.getThesisDesc());
        orderinfo.setCreateTime(new Date());
        return orderinfo;
    }

}
