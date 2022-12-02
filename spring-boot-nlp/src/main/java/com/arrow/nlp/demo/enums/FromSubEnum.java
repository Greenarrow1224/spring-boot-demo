package com.arrow.nlp.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 上游业务类型枚举类
 * @date 2022-12-02 17:25
 **/
@Getter
@AllArgsConstructor
public enum FromSubEnum {

    IAT("iat","听写结果"),
    KC("kc","语义结果");

    private final String type;
    private final String desc;
}
