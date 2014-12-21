/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloServer;
import com.apollo.domain.repository.ApolloServerRepository;
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
public class ApolloServerRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloServer, String>
        implements ApolloServerRepository {

    public ApolloServerRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        //create the database and collection
        super(ApolloServer.class, mongoClient, morphia, dbName);
    }

    //find an applicaiton by name
    public ApolloServer getServerByName(String srvName) {
        Logger.error("get server by name - {}", srvName);
        return findOne(getDatastore().createQuery(ApolloServer.class).filter("name", srvName));
    }

    //find an applicaiton by name
    public ApolloServer getServerByIPAddress(String ipAddr) {
        Logger.error("get server by IP Address - {}", ipAddr);
        return findOne(getDatastore().createQuery(ApolloServer.class).filter("address", InetAddresses.forString(ipAddr)));
    }
}
