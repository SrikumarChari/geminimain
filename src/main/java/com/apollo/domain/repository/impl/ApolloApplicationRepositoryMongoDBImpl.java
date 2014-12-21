/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.model.ApolloNetwork;
import com.apollo.domain.model.ApolloServer;
import com.apollo.domain.repository.ApolloApplicationRepository;
import com.google.common.net.InetAddresses;
import com.mongodb.MongoClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.pmw.tinylog.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author schari
 */
public class ApolloApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloApplication, String>
        implements ApolloApplicationRepository {

//    private static final Logger logger = 
//            LoggerFactory.getLogger(BaseRepositoryMongoDBImpl.class);
    public ApolloApplicationRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        //create the database and collection
        super(ApolloApplication.class, mongoClient, morphia, dbName);
    }

    //find an applicaiton by name
    public ApolloApplication getAppByName(String appName) {
        Logger.debug("get app by name :{}", ToStringBuilder.reflectionToString(appName, ToStringStyle.MULTI_LINE_STYLE));
        return findOne(getDatastore().createQuery(ApolloApplication.class).filter("name", appName));

    }

    public List<ApolloNetwork> getAppNetworks(String appName) {
        ApolloApplication a = getAppByName(appName);
        return a.getNetworks();
    }

    public List<ApolloServer> getAppServers(String appName) {
        ApolloApplication a = getAppByName(appName);
        return a.getServers();
    }
    
    public List<ApolloServer> getNetworkServers (String appName, String netStart, String netEnd) {
        List<ApolloNetwork> networks = getAppNetworks(appName);
        
        for (ApolloNetwork n : networks) {
            
            if (n.getStart().getHostAddress().equals(netStart) && n.getEnd().getHostAddress().equals(netEnd)) {
                return n.getServers();
            }
        }
        return null;
    }

}
