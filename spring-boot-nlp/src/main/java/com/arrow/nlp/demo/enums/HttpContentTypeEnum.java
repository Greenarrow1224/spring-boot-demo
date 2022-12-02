package com.arrow.nlp.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description http 请求 contentType 枚举类
 * @date 2022-12-02 17:17
 **/
@Getter
@AllArgsConstructor
public enum HttpContentTypeEnum {

    JSON("application/json","json 格式"),
    XML("application/xml","xml 格式"),
    PLAIN("text/plain","纯文本格式");
    private final String contentType;
    private final String desc;
}
