/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.common.repository.impl;

import com.gemini.common.repository.BaseRepository;
import com.gemini.common.repository.Entity;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.MongoClient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.util.List;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 * @param <T>
 * @param <String>
 */
public abstract class BaseRepositoryMongoDBImpl<T extends Entity, String>
        extends BasicDAO<T, String> implements BaseRepository<T, String> {

    @Inject MongoClient mongoClient;
    @Inject Morphia morphia;
    @Inject String dbName;

    private final Class<T> type;

    @Inject
    public BaseRepositoryMongoDBImpl(@Assisted Class<T> type, MongoClient mongoClient, Morphia morphia, String dbName) {
        super(type, mongoClient, morphia, (java.lang.String) dbName);
        this.type = type;

        //map the class to the database
        morphia.map(type);
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public List<T> list() {
        Logger.debug("list-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        return getDatastore().createQuery(type).asList();
    }

    @Override
    public void update(String id, T transientObject) {
        Logger.debug("update: id: {} object: ", 
                ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE),
                ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        save(transientObject);
    }

    @Override
    public T get(String id) {
        Logger.debug("get-find id: {}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        return findOne(getDatastore().createQuery(type).filter("_id", id));
    }

    @Override
    public void add(T newInstance) {
        Logger.debug("add: {}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        save(newInstance);
    }

    @Override
    public void remove(String id) {
        Logger.debug("remove: {}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        this.deleteById(id);
    }
}
