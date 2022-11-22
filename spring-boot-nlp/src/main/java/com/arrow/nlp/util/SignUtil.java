package com.arrow.nlp.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.StringCharacterIterator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 科大讯飞平台签名工具类
 *
 * @author ren xiao fei
 * @date 2022-11-17 9:49
 **/
@Slf4j
public class SignUtil {

    private static final String HMACSHA256 = "HmacSHA256";
    private static final String SHA1 = "SHA1";
    private static final String MD5 = "MD5";


    private static final String TOKEN = "1a2d7562969ab7f7";

    /**
     * 计算 token、timestamp、rand 的 sha1 值
     * @param timestamp 	时间戳
     * @param rand	随机数
     * @return
     */
    public static String getSignature(String timestamp,String rand){
        if (StringUtils.isBlank(timestamp) || StringUtils.isBlank(rand)){
            return null;
        }
       String signatureDta = sortParams(TOKEN,timestamp,rand);
       return SecureUtil.sha1(signatureDta);
    }

    public static String getToken(){
        return TOKEN;
    }
    public static String getTokenSha1(){
       return SecureUtil.sha1(TOKEN);
    }

    /**
     * 将 token、timestamp、rand 三个参数值进行字典序排序，并拼接
     * @param token
     * @param timestamp
     * @param rand
     * @return
     */
    public static String sortParams(String token,String timestamp,String rand){
        List<String> paramList = new ArrayList<>();
        paramList.add(token);
        paramList.add(timestamp);
        paramList.add(rand);
        Collections.sort(paramList);
        return paramList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    /**
     * 设置系统级 headers
     * @param appKey
     * @param signString
     * @param headerKeys
     * @return
     */
    public static Map<String,Object> systemHeaders(String appKey, String signString, List<String> headerKeys){
        Map<String,Object>  systemHeaders = new HashMap<>();
        if (StringUtils.isBlank(appKey) || StringUtils.isBlank(signString) || CollectionUtils.isEmpty(headerKeys)){
            return systemHeaders;
        }
        StringJoiner sj = new StringJoiner(",");
        headerKeys.forEach(sj::add);
        systemHeaders.put("X-Ca-Key",appKey);
        // 不参与 headers 签名计算
        systemHeaders.put("X-Ca-Signature",signString);
        // 不参与 headers 签名计算
        systemHeaders.put("X-Ca-Signature-Headers",sj.toString());
        // 时间戳与 uuid 一起防重放
        systemHeaders.put("X-Ca-Timestamp", System.currentTimeMillis());
        systemHeaders.put("X-Ca-Nonce",UUID.randomUUID().toString().replace("-",""));
        return systemHeaders;
    }
}
