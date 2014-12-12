/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.infrastructure.persistence.mongodb;

import com.apollo.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.apollo.domain.model.application.ApolloApplication;
import com.apollo.domain.model.application.ApolloApplicationRepository;
import com.mongodb.DB;

/**
 *
 * @author schari
 */
public class ApolloApplicationRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<ApolloApplication, String>
        implements ApolloApplicationRepository {

    public ApolloApplicationRepositoryMongoDBImpl(DB db) {
        super(ApolloApplication.class, db);
    }
}
