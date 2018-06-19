package org.leo.web.rest.mapping;

/**
 * 请求映射参数类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class ControllerMappingParameter {

    private String name;
    
    private Class<?> dataType;
    
    private ControllerMappingParameterTypeEnum type;
    
    private boolean required = true;
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Class<?> getDataType() {
        return this.dataType;
    }
    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }
    
    public ControllerMappingParameterTypeEnum getType() {
        return this.type;
    }
    public void setType(ControllerMappingParameterTypeEnum type) {
        this.type = type;
    }
    
    public boolean getRequired() {
        return this.required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    
}
