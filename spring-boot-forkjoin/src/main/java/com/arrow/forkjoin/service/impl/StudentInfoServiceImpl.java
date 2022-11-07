package com.arrow.forkjoin.service.impl;

import com.arrow.forkjoin.domain.StudentInfo;
import com.arrow.forkjoin.service.StudentInfoService;
import com.arrow.forkjoin.task.ExcelImportTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description
 * @date 2022-11-07 11:44
 **/
@Service
@Slf4j
public class StudentInfoServiceImpl implements StudentInfoService {
    @Override
    public void importStudentInfo(List<StudentInfo> dbData, List<StudentInfo> excelData) {
        // 对 excel 去重
        excelData = excelData.stream().collect(Collectors.collectingAndThen
                (Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(StudentInfo::getName))), ArrayList::new));
        long startTime = System.currentTimeMillis();
        // 获取不重复的数据
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ExcelImportTask excelImportTask = new ExcelImportTask(0, excelData.size(), dbData, excelData);
        List<StudentInfo> repetitionData = forkJoinPool.invoke(excelImportTask);
        long endTime = System.currentTimeMillis();
        log.info("数据量: {} 筛选数据共计耗时: {}",dbData.size()+excelData.size(),endTime-startTime+"ms");

        //对数据进行排序
        repetitionData =repetitionData.stream()
                .sorted(Comparator.comparing(StudentInfo::getName))
                .collect(Collectors.toList());

        // TODO 后续逻辑
    }
}
