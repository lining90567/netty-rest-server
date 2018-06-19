package org.leo.web.rest.convert;

/**
 * 整数转换器
 * 
 * @author Leo
 * @date 2018/3/16
 */
final class IntegerConverter implements Converter<Integer> {

    /**
     * 类型转换
     * 
     * @param source
     * @return
     */
    @Override
    public Integer convert(Object source) {
        return Integer.parseInt(source.toString());
    }

}
