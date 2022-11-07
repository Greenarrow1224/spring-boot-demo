package com.arrow.forkjoin.task;

import com.arrow.forkjoin.domain.StudentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author greenarrrow
 * @version 1.0.0
 * @description Excel 大批量多线程导入案列
 * @date 2022-11-07 10:59
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelImportTask extends RecursiveTask<List<StudentInfo>> {

    /**
     * 单个任务处理数据量,可自定义设置
     */
    private static final Integer SINGLE_NUM = 500;
    /**
     * 开始下标
     */
    private Integer startIndex;


    /**
     * 结束下标
     */
    private Integer endIndex;

    /**
     * 需要处理的数据
     */
    private List<StudentInfo> dbData;

    /**
     * 需要处理的 excel 数据
     */
    private List<StudentInfo> excelData;




    @Override
    protected List<StudentInfo> compute() {
        // 截取数据
        excelData = excelData.subList(startIndex, endIndex);
        int tempSize = excelData.size();
        if (tempSize <= SINGLE_NUM) {
            // 获取不存在 dbData 中的数据，这里只根据姓名判断，实际场景可能存在多种状况
            return excelData.stream().filter(studentInfo ->
                    !ArrayUtils.contains(dbData.toArray(), studentInfo.getName()))
                    .collect(Collectors.toList());
        }else {
            //拆分
            int middle = (startIndex + endIndex) / 2;
            ExcelImportTask leftTask = ExcelImportTask.builder()
                    .startIndex(startIndex).endIndex(middle).dbData(dbData).excelData(excelData).build();
            ExcelImportTask rightTask = ExcelImportTask.builder()
                    .startIndex(middle+1).endIndex(endIndex).dbData(dbData).excelData(excelData).build();
            // 执行子任务并获取子任务返回结果
            leftTask.fork();
            rightTask.fork();
            // join 是阻塞的
            List<StudentInfo> leftTaskResult = leftTask.join();
            List<StudentInfo> rightTaskResult = rightTask.join();
            leftTaskResult.addAll(rightTaskResult);
            return leftTaskResult;
        }
    }
}
