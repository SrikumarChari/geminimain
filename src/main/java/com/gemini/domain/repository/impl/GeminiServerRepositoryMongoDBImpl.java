/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.repository.GeminiServerRepository;
import com.google.common.net.InetAddresses;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author schari
 */
public class GeminiServerRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiServer, String>
        implements GeminiServerRepository {

    public GeminiServerRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        //create the database and collection
        super(GeminiServer.class, mongoClient, morphia, dbName);
    }

    //find an applicaiton by name
    public GeminiServer getServerByName(String srvName) {
        Logger.error("get server by name - {}", srvName);
        return findOne(getDatastore().createQuery(GeminiServer.class).filter("name", srvName));
    }

    //find an applicaiton by name
    public GeminiServer getServerByIPAddress(String ipAddr) {
        Logger.error("get server by IP Address - {}", ipAddr);
        return findOne(getDatastore().createQuery(GeminiServer.class).filter("address", InetAddresses.forString(ipAddr)));
    }
}
