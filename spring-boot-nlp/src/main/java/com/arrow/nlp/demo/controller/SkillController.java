package com.arrow.nlp.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arrow.nlp.util.ReadBodyUtil;
import com.arrow.nlp.util.SignUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description AIUI 技能接口层
 * @date 2022-12-02 17:35
 **/
@RestController
@RequestMapping("skill")
public class SkillController {

    @Value("${xunfei.aiui.public-key}")
    private String publicKey;

    @RequestMapping("/postprocess")
    public void postprocess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String signature = httpServletRequest.getHeader("Signature");
        String body = ReadBodyUtil.readBodyString(httpServletRequest);

        // 请求校验
        boolean flag = SignUtil.checkPublicKeySignature(publicKey,signature,body);
        try {
            if (flag) {
                // 通过验证，处理请求
                String response = handleRequest(body);

                // 返回response
                httpServletResponse.setStatus(200);
                httpServletResponse.setContentType("appplication/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.getWriter().print(response);
            } else {
                // 验证未通过
                httpServletResponse.setStatus(400);
                httpServletResponse.getWriter().print("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求
     * <p>
     * 自定义技能开发主要工作:
     * 1. 处理 AIUI 发送的不同类型的请求
     * 2. 在返回完整回复前选择性地发送一些信息给用户，比如告诉用户系统正在处理中
     * 3. 对用户的请求返回合适的回复
     * <p>
     * 请求类型：
     * 1. 标准请求
     * - LaunchRequest: 用户通过入口词“打开{技能名称}”进入自定义的技能时，将会收到这个请求
     * - TextRequest: 在自定义的技能中，用户的语音请求未经过 AIUI 解析直接请求我们的服务器时，将会收到这个请求
     * - IntentRequest: 用户的语音请求经过 AIUI 解析后请求我们的服务器时，将会收到这个请求
     * - SessionEndedRequest: 用户主动退出技能结束会话时，我们的服务器将会收到这个请求。
     * <p>
     * 请求结构: 参考 Request_v2.1协议: https://aiui.xfyun.cn/doc/aiui/4_skill_develop/8_agreement/protocal/request_2.1.html
     * 请求正文参数:
     * version: 请求的版本。类型 String, 定值 2.1。必须出现
     * session: 用户的会话信息，仅包含在标准请求中。类型 Object。非必须
     * context: 设备端状态。类型 Object。必须出现
     * request: 经过 AIUI 解析的用户请求。类型 Object。必须出现
     *
     * @param requestBody request body
     * @return response json string
     */
    public String handleRequest(String requestBody) {
        /*
         * request body 示例:
         *
         * {
         *     "version": "2.1",
         *     "context": {
         *         "AudioPlayer": "",
         *         "System": {
         *             "application": {
         *                 "applicationId": "OS9071964107.yogiho",
         *                 "enable": true
         *             },
         *             "device": {
         *                 "deviceId": "1655862989770cqgjms",
         *                 "location": {},
         *                 "supportedInterfaces": null
         *             },
         *             "user": {
         *                 "accessToken": "",
         *                 "userId": "1655862989770cqgjms"
         *             }
         *         }
         *     },
         *     "session": {
         *         "new": true,
         *         "sessionId": "4dad9d7f-e06b-4286-a8d8-ce23654fbf29"
         *     },
         *     "request": {
         *         "type": "IntentRequest",
         *         "requestId": "atn07f0b626@dx0001160dc3d2a13700",
         *         "timestamp": "2022-06-22T10:00:50.924Z",
         *         "dialogState": "STARTED",
         *         "query": {
         *             "type": "TEXT",
         *             "original": "我的回合"
         *         },
         *         "intent": {
         *             "name": "turn",
         *             "score": 1,
         *             "confirmationStatus": "NONE",
         *             "slots": null
         *         }
         *     }
         * }
         */

        // 解析请求并根据请业务需求构建response
        String response = buildResponse(requestBody);

        // 返回处理后的body
        return response;
    }


    /**
     * 构建后处理 Response body
     * 返回结果默认格式见：https://aiui.xfyun.cn/doc/aiui/4_skill_develop/8_agreement/protocal/response_2.1.html
     * <p>
     * Response_2.1 body 正文参数说明:
     * version: 版本，类型 String,取值 2.1，必须出现
     * response: 返回内容,类型 Object，必须出现
     * sessionAttributes: 下一次请求的session{attributes{}}中回传至技能的属性，类型 Object, 非必须。
     * <p>
     * 返回内容对象 response 参数说明:
     * outputSpeech: 操作返回的语音文本，类型 Object，非必须
     *              - type: 输出语音类型，取值 PlainText, 纯文本
     *              - text: 文本内容
     * reprompt: 若该技能回复需要打开录音收听用户的语音回复，当用户在8秒内没有说话时，设备将推送该语音文本，用于再次提示用户输入。
     *           推送后设备再打开录音8s。若用户依旧没有说话，则会话结束。
     *           类型 Object，非必须
     *           - type: 输出语音类型，取值 PlainText, 纯文本
     *           - text: 文本内容
     * card: 用于向设备的关联APP推送消息。支持三种卡片类型：Simple、Standard、List。
     * directives: 一组指令，用于指定使用特定接口进行设备级别的操作。类型 Array。目前支持以下指令：
     *              - AudioPlayer指令
     *              - Playback指令
     *              - Dialog指令
     *              - Display指令
     *              - resolver.Data 指令，用于自定义技能结果
     * expectSpeech: 该返回是否需要设备打开麦克风进行追问。true代表要追问，默认取值为false
     * shouldEndSession: 该返回是否为会话的终点。true表示会话在响应后结束；false表示会话保持活动状态。如果未提供，则默认为true。
     *                   注意: 我们约定：若回复中包含AudioPlayer，且技能没有过多交互，此处取值必须为 true
     *
     * 针对不同的请求的响应格式:
     * - LaunchRequest: 标准响应中的所有内容的均可根据业务需求选择包含
     * - TextRequest: 标准响应中的所有内容的均可根据业务需求选择包含
     * - IntentRequest: 标准响应中的所有内容的均可根据业务需求选择包含
     * - SessionEndedRequest: 根据reason分两种情况:
     *                        - 若reason为USER_INITIATED，可以回复。回复内容中只能包括outputSpeech，且ShouldEndSession取值必须为true。
     *                        - 若reason为ERROR，不可回复。
     *
     * @param request 请求体
     * @return response body
     */
    public String buildResponse(String request) {
        /*
         * response 示例，按该示例格式返回的数据能携带自定义数据:
         * {
         *     "version": "2.1",
         *     "sessionAttributes": {
         *         "key": "value"
         *     },
         *     "response": {
         *         "outputSpeech": {
         *             "type": "PlainText",
         *             "text": "Plain text String to speak"
         *         },
         *         "directives": [
         *                        {
         * 				"type": "resolver.Data",
         * 				"data": [                       // 自定义数据列表
         *                    {
         * 						"key1": "自定义数据1",    // 用户可以根据需求自行增添编写 key:value
         * 						"key2": "自定义数据2"
         *                    }
         * 				]
         *            }
         *         ],
         *         "expectSpeech": true,
         *         "shouldEndSession": true
         *     }
         * }
         */
        // 构建responseBody
        JSONObject responseBody = new JSONObject();

        // version, 定值 2.1
        responseBody.put("version", "2.1");

        // sessionAttributes, 非必须。下一次请求所需的数据，根据自己需求设置
        JSONObject sessionAttributes = new JSONObject();
        sessionAttributes.put("key", "value");
        responseBody.put("sessionAttributes", sessionAttributes);

        // response, 根据请求类型及自己的需求设置
        JSONObject response = new JSONObject();

        // 例如: 想要返回的数据能携带自定义数据，可以构建自定义指令。data中的key:value皆可以按需求自定义
        JSONArray directives = JSONArray.parseArray("[{" +
                "\"type\": \"resolver.Data\"," +
                "\"data\": [{" +
                "\"key1\": \"自定义数据1\"," +
                "\"key2\": \"自定义数据2\"" +
                "}]" +
                "}]");
        response.put("directives", directives);

        // 例如: 想要指定操作返回的语音文本
        JSONObject outputSpeech = JSONObject.parseObject("{" +
                "\"type\": \"PlainText\"," +
                "\"text\": \"自定义的语音文本\"" +
                "}");
        response.put("outputSpeech", outputSpeech);

        responseBody.put("response", response);
        return responseBody.toJSONString();
    }

}
