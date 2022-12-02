package com.arrow.nlp.demo.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arrow.nlp.demo.enums.FromSubEnum;
import com.arrow.nlp.demo.enums.HttpContentTypeEnum;
import com.arrow.nlp.demo.response.ResponseVO;
import com.arrow.nlp.util.ReadBodyUtil;
import com.arrow.nlp.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 应用后处理接收消息
 * @date 2022-12-02 16:47
 **/
@Slf4j
@Component
public class AppPostProcessReceiveMessage implements AppPostProcessStrategy<HttpServletRequest>{
    @Value("${xunfei.aiui.token}")
    private String token;

    @Override
    public String getHttpMethodName() {
        return HttpMethod.POST.name();
    }


    /**
     * 接收消息
     * 1. 消息校验，非必须
     * 2. 解析消息主体，根据业务需求做相应处理
     * @param request
     * @return
     */
    @Override
    public String postProcess(HttpServletRequest request) {
        log.info("应用后处理接收到的消息请求: {}",request);
        String msgsignature = request.getParameter("msgsignature");
        String timestamp = request.getParameter("timestamp");
        String rand = request.getParameter("rand");
        String body = ReadBodyUtil.readBodyString(request);
        boolean flag = SignUtil.checkSignature(msgsignature, token, rand, timestamp, body);
        if (!flag) {
            log.error("====第一步: 消息验证未通过====");
            return null;
        }
        log.info("====第一步: 消息验证通过，开始进行后续处理====");
        // 解析消息主体
        log.info("====第二步: 解析消息主体====");
        String response = null;
        if (body != null && body.trim().length() > 0) {
            String contentType = request.getContentType();
            if (HttpContentTypeEnum.JSON.getContentType().equals(contentType)) {
                JSONObject jsonBody = JSON.parseObject(body);
                // 解析 body 进行业务处理
                String fromSub = jsonBody.getString("FromSub");
                if (FromSubEnum.IAT.getType().equals(fromSub)) {
                    // 识别结果例子
                    /*
                     * 识别结果示例：
                     *   {
                     *       "SessionParams": "eyJhdWUiOiJyYXciLCJkdHlwZSI6ImF...",
                     *       "Msg": {
                     *           "Type": "text",
                     *           "ContentType": "json",
                     *           "Content": "eyJ0ZXh0Ijp7ImJnIjowLCJlZCI6MCwi..."
                     *       },
                     *       "AppId": "xxxxxx",
                     *       "UserId": "13935267996",
                     *       "CreateTime": 1655104352,
                     *       "UserParams": "",
                     *       "FromSub": "iat", // iat
                     *       "MsgId": "ara8a3119a0@dx000116022f60a10c001"
                     *   }
                     *
                     * Msg.Content Base64解码示例:
                     * {"text":{"bg":0,"ed":0,"ls":false,"sn":1,"ws":[{"bg":0,"cw":[{"sc":0,"w":"合肥"}]},{"bg":0,"cw":[{"sc":0,"w":"明天"}]},{"bg":0,"cw":[{"sc":0,"w":"的"}]},{"bg":0,"cw":[{"sc":0,"w":"天气"}]}]}}
                     */
                    // 识别结果解析
                    // 这里需要自己变更对应识别结果
                    // 若不需要变更识别结果，可以将body原样返回
                    response = body;
                }else if (FromSubEnum.KC.getType().equals(fromSub)) {
                    // 语义结果解析

                    /*
                     * 语义结果示例：
                     * {
                     *      "SessionParams": "eyJhdWUiOiJyYXciLCJkdHlw...",
                     *      "Msg": {
                     *          "Type": "text",
                     *          "ContentType": "json",
                     *          "Content": "eyJ0ZXh0Ijp7ImJnIjowLCJlZCI6MCwibHMiOmZhbHN..."
                     *       },
                     *       "AppId": "xxxxxx",
                     *       "UserId": "13935267996",
                     *       "CreateTime": 1655104352,
                     *       "UserParams": "",
                     *       "FromSub": "kc",  // kc
                     *       "MsgId": "ara8a3119a0@dx000116022f60a10c001"
                     * }
                     *
                     * Msg.Content Base64解码示例:
                     * {
                     *       "intent": {
                     *           "answer": {
                     *               "text": "今天合肥市全天阴，气温20℃ ~ 29℃，空气质量优，有东南风，有点热，适合穿短袖短裙等夏季清凉衣物。",
                     *               "type": "T"
                     *           },
                     *           "category": "IFLYTEK.weather",
                     *           "data": {
                     *               "result": [...]  // 16items
                     *           },
                     *           "dialog_stat": "DataValid",
                     *           "rc": 0,
                     *           "save_history": true,
                     *           "semantic": [
                     *               {
                     *                   "intent": "QUERY",
                     *                   "slots": [
                     *                       {
                     *                           "name": "queryType",
                     *                           "value": "内容"
                     *                       },
                     *                       {
                     *                           "name": "subfocus",
                     *                           "value": "天气状态"
                     *                       }
                     *                   ]
                     *               }
                     *           ],
                     *           "service": "weather",
                     *           "sid": "ara24daa655@dx00011602308fa15800",
                     *           "state": {
                     *               "fg::weather::default::default": {
                     *                   "state": "default"
                     *               }
                     *           },
                     *           "text": "合肥今天的天气",
                     *           "used_state": {
                     *               "state": "default",
                     *               "state_key": "fg::weather::default::default"
                     *           },
                     *           "uuid": "ara24daa655@dx00011602308fa15800",
                     *           "version": "162.0"
                     *       }
                     *   }
                     */
                    // 业务处理, 根据需求处理请求，组装response
                    response = handleNlpResult(jsonBody);
                    System.out.println("语义处理结果：" + response);

                }
            }
        }
        return null;
    }


    public String handleNlpResult(JSONObject jsonObject) {
        // 根据需求组装语义结果
        // 例如: 自定义合成的文本, 需要按照对应格式组装 tts 参数并返回
        /*
         * 后处理+合成配置时，后处理服务返回结果需要满足下面格式，合成服务才能获取到合成文本
         * {"intent" : { "answer" : {"text":"xxxx"}}}
         */
        String response = null;
        JSONObject msg = jsonObject.getJSONObject("Msg");
        if (msg != null && "json".equals(msg.getString("ContentType"))) {
            String content = new String(Base64.getDecoder().decode(msg.getString("Content")), StandardCharsets.UTF_8);
            // 组成tts参数，这里设置的合成文本为语义结果中answer.text
            JSONObject jsonContent = JSON.parseObject(content);
            JSONObject jsonIntent = jsonContent.getJSONObject("intent");
            Map<String, Object> resMap = new HashMap<>(1);
            Map<String, Object> intent = new HashMap<>(1);
            JSONObject answer = jsonIntent.getJSONObject("answer");
            if (answer != null) {
                intent.put("answer", answer);
            }
            resMap.put("intent", intent);
            response = JSON.toJSONString(resMap);
        }
        // 返回处理后的语义结果
        return response;
    }
}
