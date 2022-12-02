package com.arrow.nlp.demo.component;

import com.arrow.nlp.demo.response.ResponseVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ren xiao fei
 * @version 1.0.0
 * @description 应用后处理策略工厂类
 * @date 2022-12-02 16:34
 **/
@Component
public class AppPostProcessStrategyFactory<T> implements InitializingBean {
    @Resource
    private ApplicationContextHelper applicationContextHelper;

    private static Map<String,AppPostProcessStrategy> strategyMap = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() {
        Map<String, AppPostProcessStrategy> appPostProcessStrategyMap = applicationContextHelper.getBeansOfType(AppPostProcessStrategy.class);
        for (Map.Entry<String, AppPostProcessStrategy> appPostProcessStrategyEntry : appPostProcessStrategyMap.entrySet()) {
            strategyMap.put(appPostProcessStrategyEntry.getValue().getHttpMethodName(),appPostProcessStrategyEntry.getValue());
        }

    }

    public String doPostProcessHandler(String methodName, T t) {
        return strategyMap.get(methodName).postProcess(t);
    }
}
