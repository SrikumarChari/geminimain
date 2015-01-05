/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover.mapper;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.dozer.DozerBeanMapper;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class ApolloMapperModule extends AbstractModule {

    @Override
    protected void configure() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        try {
            mapper.addMapping(new FileInputStream("/Users/schari/NetBeansProjects/SriDiscover/Properties/DTOMapping.xml"));
        } catch (FileNotFoundException ex) {
            Logger.error("DTOMapping file not found!!");
        }
        bind(DozerBeanMapper.class).toInstance(mapper);
        bind(ApolloMapper.class).in(Singleton.class);
    }
}
