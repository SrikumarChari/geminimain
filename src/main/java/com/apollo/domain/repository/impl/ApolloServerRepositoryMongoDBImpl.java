/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloServer;
import com.apollo.domain.repository.ApolloServerRepository;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Datastore;
import org.pmw.tinylog.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author schari
 */
public class ApolloServerRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloServer, String>
        implements ApolloServerRepository {

    public ApolloServerRepositoryMongoDBImpl(Datastore db) {
        //create the database and collection
        super(ApolloServer.class, db);
    }

    //find an applicaiton by name
    public ApolloServer getServerByName(String srvName) {
        Datastore ds = getDatastore();
        if (ds == null) {
            Logger.error("get server by name - no datastore:{}", srvName);
            return null;
        }

        Logger.debug("get server by name - build query for {}", srvName);
        List<ApolloServer> retList = ds.find(ApolloServer.class, "name", srvName).asList();
        for (ApolloServer a : retList) {
            //return the first one in the list
            Logger.debug("get server by name - found server: {}", srvName);
            return a;
        }
        Logger.debug("get server by name - did not find server:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        return null;
    }
}
