package com.arrow.forkjoin.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-11-16 9:42
 **/
public class EasyExcelUtil {
    /**
     * 使用 模型 来读取Excel
     *
     * @param fileInputStream Excel的输入流
     * @param tClass         模型的类
     * @return 返回 模型 的列表(为object列表,需强转)
     */
    public static <T> List<T> readExcelAllSheet(InputStream fileInputStream, Class<T> tClass) throws IOException {
        // 监听器
        AnalysisEventListenerImpl<T> listener = new AnalysisEventListenerImpl<>();
        ExcelReader excelReader = EasyExcel.read(fileInputStream, tClass, listener).build();
        excelReader.readAll();
        excelReader.finish();
        return listener.getDatas();
    }
}
