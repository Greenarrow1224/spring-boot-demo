package com.arrow.nlp.demo.component;

import com.arrow.nlp.demo.response.ResponseVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-12-02 16:42
 **/
public interface AppPostProcessStrategy<T> {

    /**
     * 获取请求方法
     * @return
     */
    String getHttpMethodName();

    /**
     * 执行不同的处理
     * @param t
     * @return
     */
    String postProcess(T t);
}
