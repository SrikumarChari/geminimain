/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloNetwork;
import com.apollo.domain.repository.ApolloNetworkRepository;
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
public class ApolloNetworkRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloNetwork, String>
        implements ApolloNetworkRepository {

    public ApolloNetworkRepositoryMongoDBImpl(Datastore db) {
        //create the database and collection
        super(ApolloNetwork.class, db);
    }

    //find an applicaiton by name
    public ApolloNetwork getNetByStartAndEnd(String start, String end) {
        Datastore ds = getDatastore();
        if (ds == null) {
            Logger.error("get networks by start and end - no datastore:{} to {}", start, end);
            return null;
        }

        Logger.debug("get networks by start and end - build query", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        List<ApolloNetwork> retList = ds.find(ApolloNetwork.class).filter("start", start).filter("end", end).asList();
        for (ApolloNetwork n : retList) {
            //return the first one in the list
            Logger.debug("get networks by start and end - found networks:{} to {}", start, end);
            return n;
        }
        Logger.debug("get networks by start and end - did not find the networks:{} to {}", start, end);
        return null;
    }
}