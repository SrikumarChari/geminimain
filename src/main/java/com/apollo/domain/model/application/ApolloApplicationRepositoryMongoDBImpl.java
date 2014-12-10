/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model.application;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schari
 */
public class ApolloApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloApplication, Integer> {

    private static final Logger logger = LoggerFactory
            .getLogger(ApolloApplicationRepositoryMongoDBImpl.class);

    public ApolloApplicationRepositoryMongoDBImpl(Class<ApolloApplication> type, DB db) {
        super(type, db);
    }

    @Override
    public ApolloApplication get(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(ApolloApplication newInstance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
