package com.arrow.nlp.demo.controller;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import com.arrow.nlp.demo.component.AppPostProcessStrategyFactory;
import com.arrow.nlp.demo.response.ResponseVO;
import com.arrow.nlp.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.rsa.RSASignature;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description AIUI 应用接口层
 * @date 2022-11-16 18:21
 **/
@RestController
@RequestMapping("/app")
@Slf4j
public class AppController {

    @Resource
    public AppPostProcessStrategyFactory appPostProcessStrategyFactory;
    /**
     *  应用后处理接口
     *  根据请求类型区分用途： GET 为服务器接入验证 / POST 为接口消息
     * @param request
     * @return String
     */
    @RequestMapping("/postprocess")
    public String commonSign(HttpServletRequest request){
        return appPostProcessStrategyFactory.doPostProcessHandler(request.getMethod(),request);
    }

}
