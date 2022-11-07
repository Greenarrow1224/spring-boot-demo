package com.arrow.forkjoin.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description
 * @date 2022-11-07 13:50
 **/
public class CommonUtil {


    /**
     * 判断一个元素能否加入到 Set 中去
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 判断 list 中是否包含 targetKey
     * @param sourceList
     * @param targetKey
     * @param <T>
     * @return
     */
    public static<T> Boolean isIncluded(List<T> sourceList,String targetKey){
        return ArrayUtils.contains(sourceList.toArray(), targetKey);
    }
}
