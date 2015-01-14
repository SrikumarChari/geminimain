/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.repository.GeminiApplicationRepository;
import com.mongodb.MongoClient;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiApplication, String>
        implements GeminiApplicationRepository {

    public GeminiApplicationRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        //create the database and collection
        super(GeminiApplication.class, mongoClient, morphia, dbName);
    }

    //find an applicaiton by name
    public GeminiApplication getAppByName(String appName) {
        Logger.debug("get app by name :{}", ToStringBuilder.reflectionToString(appName, ToStringStyle.MULTI_LINE_STYLE));
        return findOne(getDatastore().createQuery(GeminiApplication.class).filter("name", appName));
    }

    public List<GeminiNetwork> getAppNetworks(String appName) {
        GeminiApplication a = getAppByName(appName);
        return a.getNetworks();
    }

    public List<GeminiServer> getAppServers(String appName) {
        GeminiApplication a = getAppByName(appName);
        return a.getServers();
    }
    
    public List<GeminiServer> getNetworkServers (String appName, String netStart, String netEnd) {
        List<GeminiNetwork> networks = getAppNetworks(appName);
        
        List<GeminiNetwork> net = networks.stream()
                .filter(n -> n.getStart().getHostAddress().equals(netStart))
                .filter(n -> n.getEnd().getHostAddress().equals(netEnd))
                .collect(Collectors.toList());

        if (net != null) {
            return net.get(0).getServers();
        }

//        for (GeminiNetwork n : networks) {
//            if (n.getStart().getHostAddress().equals(netStart) && n.getEnd().getHostAddress().equals(netEnd)) {
//                return n.getServers();
//            }
//        }

        return null;
    }
}
