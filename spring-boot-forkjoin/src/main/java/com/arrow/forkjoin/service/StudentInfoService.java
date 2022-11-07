package com.arrow.forkjoin.service;

import com.arrow.forkjoin.domain.StudentInfo;

import java.util.List;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description
 * @date 2022-11-07 11:42
 **/
public interface StudentInfoService {

    /**
     * 批量导入学生
     */
    void importStudentInfo(List<StudentInfo> dbData, List<StudentInfo> excelData);
}
