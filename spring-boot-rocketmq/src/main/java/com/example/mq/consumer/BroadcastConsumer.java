package com.example.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description 广播模式消费，每个消费者消费的消息都是相同的
 * @date 2022-11-01 9:55
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = "Consumer_Broadcast",//主题
        consumerGroup = "Consumer_Broadcast_group",//消费组  唯一
        messageModel = MessageModel.BROADCASTING //消费模式 默认CLUSTERING集群  BROADCASTING:广播（接收所有信息）
)
public class BroadcastConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        try {
            //模拟业务逻辑处理中...
            log.info("BroadcastConsumer  广播模式消费 message: {}  ", message);
            TimeUnit.SECONDS.sleep(10);
            //模拟出错，触发重试
//            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
