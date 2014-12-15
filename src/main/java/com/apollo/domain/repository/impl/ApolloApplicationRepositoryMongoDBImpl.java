/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository.impl;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.repository.ApolloApplicationRepository;
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
public class ApolloApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloApplication, String>
        implements ApolloApplicationRepository {

//    private static final Logger logger = 
//            LoggerFactory.getLogger(BaseRepositoryMongoDBImpl.class);

    public ApolloApplicationRepositoryMongoDBImpl(Datastore db) {
        //create the database and collection
        super(ApolloApplication.class, db);
    }

    //find an applicaiton by name
    public ApolloApplication getAppByName(String appName) {
        Datastore ds = getDatastore();
        if (ds == null) {
            Logger.error("get-no datastore:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
            return null;
        }

        Logger.debug("get-build query", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        List<ApolloApplication> retList = ds.find(ApolloApplication.class, "name", appName).asList();
        for (ApolloApplication a : retList) {
            //return the first one in the list
            Logger.debug("get-found application:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
            return a;
        }
        Logger.debug("get-did not find application:{}", ToStringBuilder.reflectionToString(this.getClass().getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        return null;
    }
}
