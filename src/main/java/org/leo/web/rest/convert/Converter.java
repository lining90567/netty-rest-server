package org.leo.web.rest.convert;

/**
 * 数据转换器接口
 * 
 * @author Leo
 * @date 2018/3/16
 *
 * @param <S>
 * @param <T>
 */
public interface Converter<T> {

    /**
     * 类型转换
     * 
     * @param source
     * @return
     */
    T convert(Object source);

}
