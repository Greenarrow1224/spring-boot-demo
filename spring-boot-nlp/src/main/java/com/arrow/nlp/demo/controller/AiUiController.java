package com.arrow.nlp.demo.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import com.arrow.nlp.demo.response.ResponseVO;
import com.arrow.nlp.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.security.rsa.RSASignature;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description AIUI 平台测试
 * @date 2022-11-16 18:21
 **/
@RestController
@RequestMapping("/xunfei")
@Slf4j
public class AiUiController {



    @GetMapping("/sign")
    public ResponseVO<String> common(@RequestParam("signature") String signature,
                             @RequestParam("timestamp") String timestamp,
                             @RequestParam("rand") String rand){
        String systemSignature = SignUtil.getSignature(timestamp, rand);
        log.info("讯飞回调信息: signature=> {},timestamp=> {},rand=> {}",systemSignature,timestamp,rand);
        log.info("系统生成的签名: {}",systemSignature);
        if (!systemSignature.equals(signature)) {
            return null;
        }
        String token = SignUtil.getToken();
        String tokenSha1 = SignUtil.getTokenSha1();
        log.info("系统测试token: {}",token);
        log.info("系统根据token进行sha1后的结果: {}",tokenSha1);
        return ResponseVO.success(tokenSha1);
    }

}
