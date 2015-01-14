/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.mapper;

import com.gemini.domain.model.GeminiEnvironmentType;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class EnvironmentTypeCustomConverter implements CustomConverter {

    @Override
    public Object convert(Object destValue, Object sourceValue, 
            Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceValue == null) {
            Logger.error("Environment Type Custom Converter used incorrectly, NULL values passed");
            return null;
        }
        if (sourceValue instanceof String) {
            //convert String to GeminiEnvironmentType
            for (GeminiEnvironmentType envType : GeminiEnvironmentType.values()) {
                if (envType.name().equals((String)sourceValue)) {
                    return envType;
                }
            }
        } else if (sourceValue instanceof InetAddress) {
            return ((GeminiEnvironmentType) sourceValue).name();
        } else {
            Logger.error("IP Address Custom Converter used incorrectly. Arguments passed in were: source\n {} \n destination\n {}",
                    ToStringBuilder.reflectionToString(sourceValue, ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(destValue, ToStringStyle.MULTI_LINE_STYLE));
            throw new MappingException("Converter TestCustomConverter used incorrectly. Arguments passed in were:"
                    + ToStringBuilder.reflectionToString(destValue, ToStringStyle.MULTI_LINE_STYLE)
                    + " and "
                    + ToStringBuilder.reflectionToString(sourceValue, ToStringStyle.MULTI_LINE_STYLE));
        }
        return null;
    }    
}
