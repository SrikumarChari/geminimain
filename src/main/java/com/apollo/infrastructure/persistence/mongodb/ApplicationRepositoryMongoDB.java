/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.infrastructure.persistence.mongodb;

import com.apollo.domain.model.application.ApolloApplication;
import com.apollo.domain.model.application.ApolloApplicationRepository;
import com.mongodb.*;
import java.util.List;
/**
 *
 * @author schari
 */
public class ApplicationRepositoryMongoDB implements ApolloApplicationRepository {

    @Override
    public ApolloApplication find(Integer appID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ApolloApplication> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void store(ApolloApplication a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void nextApplicationID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
