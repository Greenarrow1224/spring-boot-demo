package com.example.mq.producer.impl;

import com.alibaba.fastjson.JSON;
import com.example.mq.producer.IRocketMQService;
import com.example.mq.util.MessageListSplitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description
 * @date 2022-10-31 11:35
 **/
@Slf4j
@Service
public class RocketMQServiceImpl implements IRocketMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private DefaultMQProducer mqProducer;

    @Override
    public SendResult sendMessage(String destination, Object msg) {
        String[] split = destination.split(":");
        if (split.length == 2) {
            return this.sendMessage(split[0],split[1],msg);
        }
        return this.sendMessage(destination, null, msg);
    }

    @Override
    public SendResult sendMessage(String topicName, String tags, Object msg) {
        return this.sendMessage(topicName, tags, null, msg);
    }

    @Override
    public SendResult sendMessage(String topicName, String tags, String key, Object msg) {
        MessageBuilder<?> messageBuilder = MessageBuilder.withPayload(msg);
        // 设置key,唯一标识码要设置到keys字段，方便将来定位消息丢失问题
        if (StringUtils.isNotBlank(key)) {
            messageBuilder.setHeader(MessageConst.PROPERTY_KEYS,key);
        }
        Message<?> message = messageBuilder.build();
        SendResult sendResult = this.rocketMQTemplate.syncSend(StringUtils.isBlank(tags) ? topicName : (topicName + ":" + tags), message);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("MQ发送同步消息成功,topicName={},tags={},msg={},sendResult={}", topicName, tags, msg, sendResult);
            // TODO 其他处理
        }
        return sendResult;
    }

    @Override
    public SendResult sendMessageBySql(String topicName, Map<String, Object> map, Object msg) {
        return this.sendMessageBySql(topicName, map, null, msg);
    }

    @Override
    public SendResult sendMessageBySql(String topicName, Map<String, Object> map, String key, Object msg) {
        MessageBuilder<?> messageBuilder = MessageBuilder.withPayload(msg);
        // 设置key,唯一标识码要设置到keys字段，方便将来定位消息丢失问题
        if (StringUtils.isNotBlank(key)) {
            messageBuilder.setHeader(MessageConst.PROPERTY_KEYS, key);
        }
        // 设置自定义属性
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                messageBuilder.setHeader(entry.getKey(), entry.getValue());
            }
        }
        Message<?> message = messageBuilder.build();
        SendResult sendResult = this.rocketMQTemplate.syncSend(topicName, message);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("发送同步消息-SQL92模式成功,topicName={},map={},msg={},sendResult={}", topicName, map, msg, sendResult);
            // TODO 其他处理
        }
        return sendResult;
    }

    @Override
    public void sendAsyncMessage(String destination, Object msg, SendCallback sendCallback) {
        this.rocketMQTemplate.asyncSend(destination, msg, sendCallback);
        log.info("MQ发送异步消息,destination={} msg={}", destination, msg);
    }

    @Override
    public void sendOneway(String destination, Object msg) {
        this.rocketMQTemplate.sendOneWay(destination, msg);
        log.info("MQ发送单向消息,destination={} msg={}", destination, msg);
    }

    @Override
    public void sendBatchMessage(String destination, List<?> list) {
        String topicName = destination;
        String tags = "";

        String[] split = destination.split(":");
        if (split.length == 2) {
            topicName = split[0];
            tags = split[1];
        }
        this.sendBatchMessage(topicName, tags, 30000L, list);
    }

    @Override
    public void sendBatchMessage(String topicName, String tags, Long timeout, List<?> list) {
        // 转为 message
        List<org.apache.rocketmq.common.message.Message> messages = list.stream().map(x ->
                new org.apache.rocketmq.common.message.Message(topicName, tags,
                        // String 类型不需要转 JSON，其它类型都要转为 JSON 模式
                        x instanceof String ? ((String) x).getBytes(StandardCharsets.UTF_8) : JSON.toJSONBytes(x))
        ).collect(Collectors.toList());
        MessageListSplitUtil messageListSplit = new MessageListSplitUtil(messages);
        while (messageListSplit.hasNext()) {
            try {
                List<org.apache.rocketmq.common.message.Message> listItem = messageListSplit.next();
                SendResult sendResult = mqProducer.send(listItem, timeout == null ? 30000L : timeout);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    log.info("MQ发送批量消息成功,topicName={}  tags={}， size={},sendResult={}", topicName, tags, listItem.size(), sendResult);
                }
            } catch (Exception e) {
                //处理error
                log.error("MQ发送批量消息失败,topicName={}  tags={}，,errorMessage={}", topicName, tags, e.getMessage(), e);
                throw new RuntimeException("MQ发送批量消息失败,原因：" + e.getMessage());
            }
        }
    }

    @Override
    public SendResult sendDelayLevel(String destination, Object msg, int delayTimeLevel) {
        return this.sendDelayLevel(destination, msg, 30000, delayTimeLevel);
    }

    @Override
    public SendResult sendDelayLevel(String destination, Object msg, int timeout, int delayTimeLevel) {
        Message<?> message = MessageBuilder.withPayload(msg).build();
        SendResult sendResult = this.rocketMQTemplate.syncSend(destination, message, timeout, delayTimeLevel);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("MQ发送延时消息成功,destination={} msg={} sendResult={}", destination, message, sendResult);
        }
        return sendResult;
    }

    @Override
    public SendResult sendInOrder(String destination, Object msg, String hashKey) {
        Message<?> message = MessageBuilder.withPayload(msg).build();
        // hashKey:  根据其哈希值取模后确定发送到哪一个队列
        SendResult sendResult = this.rocketMQTemplate.syncSendOrderly(destination, message, hashKey);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("MQ发送顺序消息成功,destination={} msg={} sendResult={}", destination, message, sendResult);
        }
        return sendResult;
    }

    @Override
    public SendResult sendMessageInTransaction(String destination, Object msg, Object arg) {
        //
        Message<?> message = MessageBuilder.withPayload(msg instanceof String ? msg : JSON.toJSONString(msg)).build();
        TransactionSendResult sendResult = this.rocketMQTemplate.sendMessageInTransaction(destination, message, arg);
        if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.info("MQ发送事务消息成功,destination={} msg={} sendResult={}", destination, message, sendResult);
        }
        return sendResult;
    }
}
