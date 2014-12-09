/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model.application;

import java.util.List;

/**
 *
 * @author schari
 */
public interface ApolloApplicationRepository {
    //find an application given it's ID
    ApolloApplication find(Integer appID);
    
    //returns a list of all applications
    List<ApolloApplication> findAll();
    
    //save an Application object
    void store(ApolloApplication a);
    
    //returns the next application id
    void nextApplicationID ();
}
