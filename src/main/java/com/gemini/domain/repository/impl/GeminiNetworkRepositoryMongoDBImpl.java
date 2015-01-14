/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.repository.GeminiNetworkRepository;
import com.google.common.net.InetAddresses;
import com.mongodb.MongoClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author schari
 */
public class GeminiNetworkRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiNetwork, String>
        implements GeminiNetworkRepository {

    public GeminiNetworkRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        //create the database and collection
        super(GeminiNetwork.class, mongoClient, morphia, dbName);
    }

    //find an applicaiton by name
    public GeminiNetwork getNetByStartAndEnd(String start, String end) {
        Datastore ds = getDatastore();
        if (ds == null) {
            Logger.error("get networks by start and end - no datastore:{} to {}", start, end);
            return null;
        }

        Logger.debug("get networks by start and end - build query",
                ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));

        List<GeminiNetwork> retList = ds.find(GeminiNetwork.class)
                .filter("start", InetAddresses.forString(start))
                .filter("end", InetAddresses.forString(end)).asList();
        for (GeminiNetwork n : retList) {
            //return the first one in the list
            Logger.debug("get networks by start and end - found networks:{} to {}", start, end);
            return n;
        }
        Logger.debug("get networks by start and end - did not find the networks:{} to {}", start, end);
        return null;
    }

    public List<GeminiServer> getServers(String start, String end) {
        List<GeminiServer> l = null;
        GeminiNetwork net = getNetByStartAndEnd(start, end);
        if (net != null) {
            Logger.debug("get network servers by start and end - returning servers for network {} to {}", start, end);
            l = net.getServers();
        }
        return l;
    }
}
