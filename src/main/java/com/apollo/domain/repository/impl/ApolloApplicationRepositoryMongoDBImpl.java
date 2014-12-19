/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.repository.ApolloApplicationRepository;
import com.mongodb.MongoClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
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
        Datastore dStore = getDatastore();
        if (dStore == null) {
            Logger.error("get-no datastore:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        Logger.debug("get-build query", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        List<ApolloApplication> retList = dStore.find(ApolloApplication.class, "name", appName).asList();
        for (ApolloApplication a : retList) {
            //return the first one in the list
            Logger.debug("get-found application:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
            return a;
        }
        Logger.debug("get-did not find application:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        return null;
    }

    @Override
    public void update(String id, ApolloApplication transientObject) {
        this.save(transientObject);
    }

    @Override
    public List<ApolloApplication> list() {
        return this.find().asList();
    }

    @Override
    public void add(ApolloApplication newInstance) {
        this.save(newInstance);
    }

    @Override
    public void remove(String id) {
        this.deleteById(id);
    }
}
