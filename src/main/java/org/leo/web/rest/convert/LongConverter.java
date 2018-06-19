package org.leo.web.rest.convert;

/**
 * 长整数转换器
 * 
 * @author Leo
 * @date 2018/3/16
 */
final class LongConverter implements Converter<Long> {

    /**
     * 类型转换
     * 
     * @param source
     * @return
     */
    @Override
    public Long convert(Object source) {
        return Long.parseLong(source.toString());
    }

}