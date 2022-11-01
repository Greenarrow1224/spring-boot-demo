package com.example.mq.util;

import org.apache.commons.validator.Var;
import org.apache.rocketmq.common.message.Message;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description 消息列表分割工具类
 * @date 2022-10-31 10:36
 **/
public class MessageListSplitUtil implements Iterator<List<Message>> {

    /**
     * 最大4MB，这里每次只发送1MB。（为了避免MessageListSplitUtil.calculateMessageSize计算不精确及大批量数据发送超时才设置1MB）
     */
    private final Integer SIZE_LIMIT = 1024 * 1024;

    private final List<Message> messages;
    private Integer currentIndex;

    public MessageListSplitUtil(List<Message> messages) {
        this.messages = messages;
    }


    @Override
    public boolean hasNext() {
        return currentIndex < messages.size();
    }

    @Override
    public List<Message> next() {
        Integer startIndex = getStartIndex();
        Integer nextIndex = startIndex;
        Integer totalSize = 0;
        for (; nextIndex < messages.size(); nextIndex++) {
            Message message = messages.get(nextIndex);
            Integer calculateMessageSize = calculateMessageSize(message);
            if (calculateMessageSize + totalSize > SIZE_LIMIT) {
                break;
            }else {
                totalSize += calculateMessageSize;
            }
        }
        List<Message> messages = this.messages.subList(startIndex, nextIndex);
        currentIndex = nextIndex;
        return messages;
    }


    private Integer getStartIndex() {
        Message currentMessage = messages.get(currentIndex);
        Integer calculateMessageSize = calculateMessageSize(currentMessage);
        while (calculateMessageSize > SIZE_LIMIT) {
            currentIndex+=1;
            Message message = messages.get(currentIndex);
            calculateMessageSize = calculateMessageSize(message);
        }
        return currentIndex;
    }

    /**
     * 计算消息的字节长度
     * @param message
     * @return
     */
    private Integer calculateMessageSize(Message message) {
        int tempSize = message.getTopic().length() + message.getBody().length;
        Map<String, String> properties = message.getProperties();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            tempSize += entry.getKey().length() + entry.getValue().length();
        }
        tempSize = tempSize + 20;
        return tempSize;
    }
}
