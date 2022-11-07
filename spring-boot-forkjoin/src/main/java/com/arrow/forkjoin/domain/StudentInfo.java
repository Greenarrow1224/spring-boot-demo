package com.arrow.forkjoin.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 学生信息
 * @date 2022-11-07 11:00
 **/
@Data
public class StudentInfo implements Serializable {
    private static final long serialVersionUID = -3528707841848404541L;

    private Long id;
    private String name;
}
