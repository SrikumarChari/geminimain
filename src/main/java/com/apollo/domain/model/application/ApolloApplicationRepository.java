/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model.application;

import com.apollo.common.repository.BaseRepository;

/**
 * 
 * Methods are inherited, additional methods can be added if required. All streaming,
 * database or file implementation will inherit from this class
 * 
 * 
 * @author schari
 * @param <ApolloApplication>
 * @param <Integer>
 */
public interface ApolloApplicationRepository extends BaseRepository<ApolloApplication, Integer>  {
    
}
