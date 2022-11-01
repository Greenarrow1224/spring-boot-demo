package com.example.mq.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

import java.util.List;
import java.util.Map;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description RocketMQ 生产者常用发送方法
 * @date 2022-10-31 10:51
 **/
public interface IRocketMQService {

    /**
     * 发送同步消息（不保证100%可靠投递）
     * @param destination 主题名 : 标签
     * @param msg 消息对象
     * @return 发送结果，只有为 SEND_OK 且同步 Master 服务器或同步刷盘才保证 100% 投递成功
     */
    SendResult sendMessage(String destination, Object msg);

    /**
     *
     * @param topicName 主题名
     * @param tags 标签
     * @param msg 消息对象
     * @return
     */
    SendResult sendMessage(String topicName, String tags, Object msg);

    /**
     *
     * @param topicName 主题名
     * @param tags 标签
     * @param key 唯一标识码，方便将来定位消息丢失问题
     * @param msg 消息对象
     * @return
     */
    SendResult sendMessage(String topicName, String tags, String key, Object msg);


    /**
     * 发送同步消息-SQL92模式
     * @param topicName 主题名
     * @param map 自定义属性
     * @param msg 消息对象
     * @return
     */
    SendResult sendMessageBySql(String topicName, Map<String, Object> map, Object msg);


    /**
     * 发送同步消息-SQL92模式
     * @param topicName
     * @param map
     * @param key 唯一标识码，方便将来定位消息丢失问题
     * @param msg
     * @return
     */
    SendResult sendMessageBySql(String topicName, Map<String, Object> map,String key, Object msg);


    /**
     * 发送异步消息
     * @param destination
     * @param msg
     * @param sendCallback 异步回调函数
     */
    void sendAsyncMessage(String destination, Object msg, SendCallback sendCallback);

    /**
     * 发送单向消息
     * @param destination 主题名:标签
     * @param msg
     */
    void sendOneway(String destination, Object msg);


    /**
     * 发送批量消息
     * @param destination
     * @param list
     */
    void sendBatchMessage(String destination, List<?> list);


    /**
     * 发送批量消息
     * @param topicName
     * @param tags
     * @param timeout
     * @param list
     */
    void sendBatchMessage(String topicName, String tags, Long timeout, List<?> list);


    /**
     * 发送延时消息（超时时间默认3s）
     * @param destination
     * @param msg
     * @param delayTimeLevel 延时等级(从1开始)
     * @return
     */
    SendResult sendDelayLevel(String destination, Object msg, int delayTimeLevel);
    SendResult sendDelayLevel(String destination, Object msg, int timeout,int delayTimeLevel);

    /**
     * 发送顺序消息（分区有序,多个queue参与，即相对每个queue，消息都是有序的。）
     * @param destination
     * @param msg
     * @param hashKey 根据其哈希值取模后确定发送到哪一个queue队列
     * @return
     */
    SendResult sendInOrder(String destination, Object msg, String hashKey);

    /**
     * 发送事务消息
     * 事务消息使用上的限制
     * 1:事务消息不支持延时消息和批量消息。
     * 2:为了避免单个消息被检查太多次而导致半队列消息累积，我们默认将单个消息的检查次数限制为 15 次，但是用户可以通过 Broker 配置文件的 transactionCheckMax参数来修改此限制。如果已经检查某条消息超过 N 次的话（ N = transactionCheckMax ） 则 Broker 将丢弃此消息，并在默认情况下同时打印错误日志。用户可以通过重写 AbstractTransactionalMessageCheckListener 类来修改这个行为。
     * 3:事务消息将在 Broker 配置文件中的参数 transactionTimeout 这样的特定时间长度之后被检查。当发送事务消息时，用户还可以通过设置用户属性 CHECK_IMMUNITY_TIME_IN_SECONDS 来改变这个限制，该参数优先于 transactionTimeout 参数。
     * 4:事务性消息可能不止一次被检查或消费。
     * 5:提交给用户的目标主题消息可能会失败，目前这依日志的记录而定。它的高可用性通过 RocketMQ 本身的高可用性机制来保证，如果希望确保事务消息不丢失、并且事务完整性得到保证，建议使用同步的双重写入机制。
     * 6:事务消息的生产者 ID 不能与其他类型消息的生产者 ID 共享。与其他类型的消息不同，事务消息允许反向查询、MQ服务器能通过它们的生产者 ID 查询到消费者。
     *
     * @param destination 主题名:标签 topicName:tags
     * @param msg         发送对象
     * @param arg         arg
     * @return 发送结果，只有为SEND_OK且同步Master服务器或同步刷盘才保证100%投递成功
     */
    SendResult sendMessageInTransaction(String destination, Object msg, Object arg);

}
