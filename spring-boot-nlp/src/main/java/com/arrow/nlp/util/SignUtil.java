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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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

    // 后期优化到 Nacos 配置文件中
    private static final String TOKEN = "c5f8ccdc5b9a9454";
    // 动态实体密钥
    private static final String ACCOUNT_KEY = "3f7f7a53b8504eddb411dda18c5c5759";
    // 动态实体密钥
    private static final String NAMESPACE = "OS14956178533";



    private static final String UPLOAD_URL = "https://openapi.xfyun.cn/v2/aiui/entity/upload-resource";
    private static final String CHECK_URL = "https://openapi.xfyun.cn/v2/aiui/entity/check-resource";
    private static final String X_NONCE = "12";
    private static final String APPID = "";
    private static final String X_NAMESPACE = "";
    private static final String ACCOUNTKEY = "";

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
     * @param namespace
     * @return
     */
    public static Map<String,Object> systemHeaders(String namespace){
        Map<String,Object>  systemHeaders = new HashMap<>();
        if (StringUtils.isBlank(namespace)){
            return systemHeaders;
        }
        String curTime = System.currentTimeMillis() / 1000 + "";
        // aiui开放平台的命名空间，在「技能工作室-我的实体-动态实体密钥」中查看
        systemHeaders.put("X-NameSpace",namespace);
        // 随机数（最大长度128个字符）
        systemHeaders.put("X-Ca-Nonce",UUID.randomUUID().toString().replace("-",""));
        // 当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String)
        systemHeaders.put("X-CurTime", curTime);
        // MD5(accountKey + Nonce + CurTime),三个参数拼接的字符串，进行MD5哈希计算
        systemHeaders.put("X-CheckSum", System.currentTimeMillis());

        return systemHeaders;
    }


    /**
     * 生成握手参数
     * @param appId
     * @param secretKey
     * @return
     */
    public static String getHandShakeParams(String appId, String secretKey) {
        String ts = System.currentTimeMillis()/1000 + "";
        String signa = "";
        try {
            signa = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(appId + ts), secretKey);
            return "?appid=" + appId + "&ts=" + ts + "&signa=" + URLEncoder.encode(signa, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 检验signature方法
     * 用于签名验证
     * <p>
     * 加密/校验流程如下：
     * 1. 将参数值args集合进行字典序排序
     * 2. 参数字符串拼接成一个字符串进行sha1加密
     * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于AIUI服务
     *
     * @param signature 签名
     * @param args      参数列表
     *                  # token: 开放平台token值
     *                  # rand: 随机数
     *                  # timestamp: 时间戳
     *                  # data: request body
     * @return boolean of check result
     */
    public static Boolean checkSignature(String signature, String... args){
        // 将参数值进行字典序排序
        List<String> signList = new ArrayList<>();
        Collections.addAll(signList, args);
        Collections.sort(signList);
        String sign = signList.stream().map(String::valueOf).collect(Collectors.joining());
        String tmpStr = SecureUtil.sha1(sign);
        // 加密后的字符串与 signature 对比
        return signature != null && signature.equals(tmpStr);
    }

    /**
     * 检验 signature 方法
     * 用于签名验证
     * <p>
     * 签名验证流程:
     * 1. 对 Signature的值进行 Base64-decode,得到 decoded_signature
     * 2. 使用 SHA-1 摘要算法（十六进制编码）对请求 Body 生成 hash
     * 3. 使用 RSA 算法, 使用公钥 public_key 对 decoded_signature,hash 进行校验，摘要类型为 sha256
     *
     * @param publicKey 签名公钥
     * @param signature 签名
     * @param body      请求Body
     * @return boolean of check result
     */
    public static Boolean checkPublicKeySignature(String publicKey, String signature, String body) {
        // 对Signature的值进行Base64-decode
        byte[] decodedSignature = Base64.decodeBase64(signature);

        // 对请求Body生成hash
        String hash = SecureUtil.sha1(body);

        // 使用公钥public_key对decoded_signature,hash进行校验（SHA256withRSA）
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(pubKey);
            sign.update(hash.getBytes(StandardCharsets.UTF_8));
            return sign.verify(decodedSignature);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }
}
