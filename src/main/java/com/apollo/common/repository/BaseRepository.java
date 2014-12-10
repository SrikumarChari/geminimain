/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.common.repository;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author schari
 * @param <T>
 * @param <PK>
 */
public interface BaseRepository <T, PK extends Serializable> {

    /** Persist the newInstance object into database
     * @param newInstance */
    void add(T newInstance);

    /** Retrieve an object that was previously persisted to the database using
     *   the indicated id as primary key
     * @param id
     * @return 
     */
    T get(PK id);

    /** Save changes made to a persistent object.
     * @param transientObject */
    void update(T transientObject);

    /** Remove an object from persistent storage in the database
     * @param id */
    void delete(PK id);
    
    List<T> list();
    
}