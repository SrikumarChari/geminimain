/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.repository;

import com.apollo.common.repository.BaseRepository;
import com.apollo.domain.model.ApolloApplication;

/**
 * 
 * Methods are inherited, additional methods can be added if required. All streaming,
 * database or file implementation will inherit from this class
 * 
 * 
 * @author schari
 */
public interface ApolloApplicationRepository extends BaseRepository<ApolloApplication, String>  {
    
}
