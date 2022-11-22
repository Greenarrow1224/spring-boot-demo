package com.arrow.forkjoin.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFHeader;

import java.util.List;
import java.util.Map;

/**
 * TODO something
 * @author ren xiao fei
 * @version 1.0.0
 * @description
 * @date 2022-11-16 11:41
 **/
@Slf4j
public class AnalysisEventListenerImpl<T> extends AnalysisEventListener<T> {

    /**
     * 每隔100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);


    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            // 读取完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    /**
     * 这里收尾的原因为：
     * 比如我定义的 BACTH_COUNT=100, 每次满100条我才回调，我 excel 里只有 10 条记录，那么就不能在 invoke 方法里回调了
     * 所以这里一定要进行收尾。
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        getDatas();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        super.onException(exception, context);
    }


    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        super.extra(extra, context);
    }


    /**
     * 这里会一行行的返回头
     *  @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        //log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        ReadCellData<?> readCellData = headMap.get(0);
        String format = readCellData.getDataFormatData().getFormat();
        CellDataTypeEnum type = readCellData.getType();
        // 如果想转成成 Map<Integer,String>
        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
        Map<Integer, String> headerMap = ConverterUtils.convertToStringMap(headMap, context);
        System.out.println(headerMap);
    }



    @Override
    public boolean hasNext(AnalysisContext context) {
        return super.hasNext(context);
    }
    public List<T> getDatas(){
       return cachedDataList;
    }
}
