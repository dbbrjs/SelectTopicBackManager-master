package com.topicmanager.service;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.topicmanager.enums.ApplyThesisStatusEnum;
import com.topicmanager.enums.ThesisStatusEnum;
import com.topicmanager.fastdfs.FastDFSClientWrapper;
import com.topicmanager.mapper.*;
import com.topicmanager.pojo.*;
import com.topicmanager.result.ListResult;
import com.topicmanager.result.Result;
import com.topicmanager.result.ThesisResult;
import com.topicmanager.utils.IDgenerator;
import com.topicmanager.utils.ThesisStatus;
import com.topicmanager.vo.ThesisVo;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.mockito.cglib.core.ClassGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.annotation.Order;
import tk.mybatis.mapper.entity.Example;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

@Service
public class ThesisService {


    @Autowired
    private ThesisMapper thesisMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ApplyThesisMapper applyThesisMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CollegeHeadMapper collegeHeadMapper;

    @Autowired
    private FastDFSClientWrapper fastDFSClient;


    //教师姓名获取教师课题
    public List<Thesis> getThesisByTeacherName(String teacherName){
        Example example = new Example(Thesis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("teacher", teacherName)
                .andCondition("student is null");
        List<Thesis> thesises = thesisMapper.selectByExample(example);
//        Thesis thesis = new Thesis();
//        thesis.setTeacher(teacherName);
//        List<Thesis> thesises = thesisMapper.select(thesis);
//        System.out.println(thesises);
        return thesises;
    }
    //获取课题
    public ListResult getThesisByCollegeHead(int pageNum, int pageSize, String headId,String topic,
                                               String  stName,String teName){
        CollegeHead collegeHead = collegeHeadMapper.selectByPrimaryKey(headId);
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Thesis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thesisCollege", collegeHead.getCollege());
        if (topic!=null){
            criteria.andLike("thesisName","%"+topic+"%");
        }
        if (stName!=null){
            criteria.andLike("student","%"+stName+"%");
        }
        if (teName!=null){
            criteria.andLike("teacher","%"+teName+"%");
        }
        List<Thesis> thesises = thesisMapper.selectByExample(example);
        int count = thesisMapper.selectCountByExample(example);
        PageInfo<Thesis> info = new PageInfo<>(thesises);
        System.out.println(info.getList());
        ListResult studentResult = new ListResult(info.getList(),count);

        return studentResult;
    }
    //通过id删除课题
    public Integer deleteById(String thesisId, String thesisName){
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setThesisName(thesisName);
        Orderinfo o = orderInfoMapper.selectOne(orderinfo);
        if (o != null){
            return 0;     //课题已被选择，不能删除
        }
        Thesis t = new Thesis();
        t.setThesisId(thesisId);
        return thesisMapper.deleteByPrimaryKey(t);
    }

    //教师添加课题
    @Transactional
    public Integer addThesis(ThesisVo thesisVo){
        Applythesis applythesis = thesisVo_applyThesis(thesisVo);
        return applyThesisMapper.insert(applythesis);
    }

    //select thesis by id
    public Thesis getThesisById(String thesisId) {
        Thesis t = new Thesis();
        t.setThesisId(thesisId);
        return thesisMapper.selectByPrimaryKey(t);
    }


    //teacher 根据id修改课题
    @Transactional
    public Integer editThesis(ThesisVo thesisVo, String thesisId) {
        Thesis thesis = thesisVo_thesis(thesisVo);
        thesis.setThesisId(thesisId);
        return thesisMapper.updateByPrimaryKey(thesis);
    }

    //获取所有课题   分页查询
    public List<Thesis> getAllThesis(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Thesis> thesisList = thesisMapper.selectAll();
        PageInfo<Thesis> info = new PageInfo<>(thesisList);
        return info.getList();
    }

    //获取课题总数
    public Integer getCountThesis() {
        return  thesisMapper.selectCount(new Thesis());
    }

    //模糊条件查询
    public ThesisResult getThesis(String sisName, String sisTeacher, String sisCollege, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Thesis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("thesisName", "%" + sisName + "%")
                .andLike("teacher", "%" + sisTeacher + "%")
                .andLike("thesisCollege", "%" + sisCollege + "%")
                .andCondition("student is null")
                .andCondition("ischoose is null");
        List<Thesis> list = thesisMapper.selectByExample(example);

        int count = thesisMapper.selectCountByExample(example);
        PageInfo<Thesis> info = new PageInfo<>(list);
        System.out.println(info.getList());
        ThesisResult result = new ThesisResult(info.getList(), count);
        return result;
    }


    public List<Applythesis> teacherGetApplyThesis(String teacherName){
        Applythesis applythesis = new Applythesis();
        applythesis.setTeacher(teacherName);
        applythesis.setThesisStatus(ApplyThesisStatusEnum.WAIT_TEACHER_CHECK.getStatus());
        return applyThesisMapper.select(applythesis);
    }
    //学生申报课题
    @Transactional
    public Integer applyThesis(ThesisVo thesisVo, String studentId) {
        Integer isOk = studentService.isChoose(studentId);    //判断是否已经选题
        if(isOk == 1){
            Thesis thesis = thesisVo_thesis(thesisVo);
            Applythesis applythesis = thesisVo_applyThesis(thesisVo);
            Orderinfo orderinfo = thesisVo_Orderinfo(thesisVo, studentId);
            orderinfo.setSisNum(thesis.getThesisId());
            thesisMapper.insert(thesis);
            applythesis.setThesisStatus(ApplyThesisStatusEnum.WAIT_TEACHER_CHECK.getStatus());
            return  applyThesisMapper.insert(applythesis);

        }else{
            return 0;   //选题失败
        }
    }
    public ListResult getApplyThesisByCollegeHead(int pageNum, int pageSize, String headId,String topic,
                                                  String  stName,String teName){
        CollegeHead collegeHead = collegeHeadMapper.selectByPrimaryKey(headId);
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Applythesis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thesisCollege", collegeHead.getCollege());
//        criteria.andNotEqualTo("thesisStatus", ApplyThesisStatusEnum.FINISH.getStatus());
        if (topic!=null){
            criteria.andLike("thesisName","%"+topic+"%");
        }
        if (stName!=null){
            criteria.andLike("studentName","%"+stName+"%");
        }
        if (teName!=null){
            criteria.andLike("teacher","%"+teName+"%");
        }
        List<Applythesis> applythesis = applyThesisMapper.selectByExample(example);
        int count = applyThesisMapper.selectCountByExample(example);
        PageInfo<Applythesis> info = new PageInfo<>(applythesis);
        ListResult studentResult = new ListResult(info.getList(),count);
        return studentResult;
    }
    public void changeApplyThesisStatus(String applyThesisId,Integer status){
        Applythesis applythesis = new Applythesis();
        applythesis.setThesisStatus(status);
        applythesis.setThesisId(applyThesisId);
        applyThesisMapper.updateByPrimaryKeySelective(applythesis);

        if (status.equals(ApplyThesisStatusEnum.FINISH.getStatus())){
            Applythesis applythesis1 = applyThesisMapper.selectByPrimaryKey(applythesis);
            if (applythesis1.getStudentName()!=null){
                Orderinfo orderinfo = applythesisesis_orderinfo(applythesis1);
                Student student = new Student();
                student.setStudentName(applythesis1.getStudentName());
                Student student1 = studentMapper.selectOne(student);
                orderinfo.setStuNum(student1.getStudentId());
                orderinfo.setStatus("待审核");
                orderinfo.setModel("学生自建课题");
                orderInfoMapper.insert(new Orderinfo());
            }else {
                Thesis thesis = applythesis_thesis(applythesis1);
                thesisMapper.insert(thesis);
            }
        }
    }

    public Thesis thesisVo_thesis(ThesisVo thesisVo){
        Thesis thesis = new Thesis();
        thesis.setThesisId(IDgenerator.generatorThesisId());
        thesis.setAllowSpecial(thesisVo.getAllowSpecial());
        thesis.setThesisName(thesisVo.getThesisName());
        thesis.setThesisCollege(thesisVo.getThesisCollege());
        thesis.setThesisType(thesisVo.getThesisType());
        thesis.setThesisFrom(thesisVo.getThesisFrom());
        thesis.setTeacher(thesisVo.getTeacher());
        thesis.setModel(thesisVo.getModel());
        thesis.setClassroom(thesisVo.getClassroom());
        Date date = new Date();
        thesis.setThesisDate(date);
        thesis.setThesisDoc(thesisVo.getFilePath());
        thesis.setThesisDesc(thesisVo.getThesisDesc());
        thesis.setStudent(thesisVo.getStudent());
        thesis.setIschoose(null);
        return thesis;
    }
    public Thesis applythesis_thesis(Applythesis applythesis){
        Thesis thesis = new Thesis();
        thesis.setThesisId(IDgenerator.generatorThesisId());
        thesis.setThesisName(applythesis.getThesisName());
        thesis.setThesisCollege(applythesis.getThesisCollege());
        thesis.setThesisType(applythesis.getThesisType());
        thesis.setThesisFrom(applythesis.getThesisFrom());
        thesis.setTeacher(applythesis.getTeacher());
        Date date = new Date();
        thesis.setThesisDate(date);
        thesis.setThesisDesc(applythesis.getThesisDesc());
        thesis.setIschoose(null);
        return thesis;
    }

    public Orderinfo applythesisesis_orderinfo(Applythesis applythesis){
        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setId(IDgenerator.generatorOrderId());
        orderinfo.setThesisName(applythesis.getThesisName());
        orderinfo.setTeacher(applythesis.getTeacher());
        orderinfo.setStudent(applythesis.getStudentName());
        orderinfo.setThesisCollege(applythesis.getThesisCollege());
        orderinfo.setThesisFrom(applythesis.getThesisFrom());
        orderinfo.setThesisType(applythesis.getThesisType());
        orderinfo.setThesisDesc(applythesis.getThesisDesc());
        return orderinfo;
    }
    public Applythesis thesisVo_applyThesis(ThesisVo thesisVo) {
        Applythesis applythesis = new Applythesis();
        applythesis.setThesisId(IDgenerator.generatorStuSisId());
        applythesis.setThesisName(thesisVo.getThesisName());
        applythesis.setStudentName(thesisVo.getStudent());
        applythesis.setTeacher(thesisVo.getTeacher());
        applythesis.setThesisCollege(thesisVo.getThesisCollege());
        applythesis.setThesisDoc(thesisVo.getFilePath());
        applythesis.setThesisFrom(thesisVo.getThesisFrom());
        applythesis.setThesisStatus(ThesisStatusEnum.PENDING.getStatus());      //待审核状态
        applythesis.setThesisType(thesisVo.getThesisType());
        applythesis.setThesisDesc(thesisVo.getThesisDesc());
        Date date = new Date();
        applythesis.setThesisDate(date);
        return applythesis;
    }

    public Orderinfo thesisVo_Orderinfo(ThesisVo thesisVo, String studentId){
        Orderinfo o = new Orderinfo();
        o.setId(IDgenerator.generatorOrderId());
        o.setStuNum(studentId);
        o.setThesisName(thesisVo.getThesisName());
        o.setThesisCollege(thesisVo.getThesisCollege());
        o.setThesisType(thesisVo.getThesisType());
        o.setThesisFrom(thesisVo.getThesisFrom());
        o.setClassroom(thesisVo.getClassroom());
        o.setTeacher(thesisVo.getTeacher());
        o.setModel(thesisVo.getModel());
        o.setAllowSpecial(thesisVo.getAllowSpecial());
        o.setThesisDate(new Date());
        o.setThesisDoc(thesisVo.getFilePath());
        o.setThesisDesc(thesisVo.getThesisDesc());
        o.setStudent(thesisVo.getStudent());
        o.setCreateTime(new Date());
        o.setStatus(ThesisStatus.PENDING);
        return o;
    }

    public void uploadReport(String stId, MultipartFile file){
        try {
            String path = fastDFSClient.uploadFile(file);
            path = "http://112.124.14.194/" + path;
            Orderinfo orderinfo = new Orderinfo();
            orderinfo.setStuNum(stId);
            Orderinfo orderinfo1 = orderInfoMapper.selectOne(orderinfo);
            orderinfo1.setReportDoc(path);
            orderInfoMapper.updateByPrimaryKey(orderinfo1);
        }
        catch (Exception e){

        }
    }
    public void uploadPaper(String stId, MultipartFile file){
        try {
            String path = fastDFSClient.uploadFile(file);
            path = "http://112.124.14.194/" + path;
            Orderinfo orderinfo = new Orderinfo();
            orderinfo.setStuNum(stId);
            Orderinfo orderinfo1 = orderInfoMapper.selectOne(orderinfo);
            orderinfo1.setPaperDoc(path);
            orderInfoMapper.updateByPrimaryKey(orderinfo1);
        }
        catch (Exception e){

        }
    }


}
