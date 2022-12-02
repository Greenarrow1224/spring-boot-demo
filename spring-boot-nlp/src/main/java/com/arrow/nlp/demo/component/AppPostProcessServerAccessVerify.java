package com.arrow.nlp.demo.component;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import com.arrow.nlp.demo.response.ResponseVO;
import com.arrow.nlp.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 应用后处理服务器接入验证
 * @date 2022-12-02 16:50
 **/
@Slf4j
@Component
public class AppPostProcessServerAccessVerify implements AppPostProcessStrategy<HttpServletRequest>{

    @Value("${xunfei.aiui.token}")
    private String token;

    @Override
    public String getHttpMethodName() {
        return HttpMethod.GET.name();
    }

    @Override
    public String postProcess(HttpServletRequest request) {
        log.info("====服务器接入验证====");
        // 获取 GET 请求携带的参数
        String rand = request.getParameter("rand");
        String timestamp = request.getParameter("timestamp");
        String signature = request.getParameter("signature");
        Boolean flag = SignUtil.checkSignature(signature, token, rand, timestamp);
        if (!flag) {
           log.error("====应用后处理服务器接入验证未通过通过====");
           return null;
        }
        log.info("====应用后处理服务器接入验证通过====");
        return SecureUtil.sha1(token);
    }
}
