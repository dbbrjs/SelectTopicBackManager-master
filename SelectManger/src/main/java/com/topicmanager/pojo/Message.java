package com.topicmanager.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@Table(name = "message")
public class Message {
    @Id
    private String messageId;
    private String studentId;
    private String teacherId;
    private Timestamp createTime;
    private Integer direct;
    private String news;
}
