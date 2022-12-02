package com.arrow.nlp.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 开放接口枚举类
 * @date 2022-12-02 16:18
 **/
@Getter
@AllArgsConstructor
public enum OpenApiEum {

    WEB_ENTITY_UPLOAD_URL("/v2/aiui/entity/upload-resource","动态实体上传"),
    WEB_ENTITY_CHECK_URL("/v2/aiui/entity/check-resource","动态实体上传结果校验");

    private final String url;
    private final String desc;
}
