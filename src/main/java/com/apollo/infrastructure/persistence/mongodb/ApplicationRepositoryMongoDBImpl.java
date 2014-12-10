/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.infrastructure.persistence.mongodb;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.application.ApolloApplication;
import com.apollo.domain.model.application.ApolloApplicationRepository;
import com.mongodb.*;

/**
 * The BaseRepositoryMongoDBImpl class provides the CRUD implementations. That 
 * class uses the class name as the database name. 
 * @author schari
 */
public class ApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl <ApolloApplication, Integer> implements ApolloApplicationRepository {

    public ApplicationRepositoryMongoDBImpl(Class<ApolloApplication> type, DB db) {
        super(type, db);
    }
}
