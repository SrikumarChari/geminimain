/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.common.repository.impl;

import com.apollo.common.repository.BaseRepository;
import com.apollo.common.repository.Entity;
import com.mongodb.WriteResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.util.List;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 * @param <T>
 * @param <String>
 */
public abstract class BaseRepositoryMongoDBImpl<T extends Entity, String>
        implements BaseRepository<T, String> {

    private final Class<T> type;
    private final Datastore ds;

//    public static final Logger Logger = LoggerFactory
//            .getLogger(BaseRepositoryMongoDBImpl.class);

    public BaseRepositoryMongoDBImpl(Class<T> type, Datastore db) {
        this.type = type;
        this.ds = db;
    }

    public Class<T> getType() {
        return type;
    }

    protected Datastore getDatastore() {
        return ds;
    }
    
    @Override
    public List<T> list() {
        Logger.debug("list-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        Query<T> ret = ds.find(type);
        return ret.asList();
    }

    @Override
    public void delete(String id) {
        //build query with id.
        Logger.debug("delete-build query and delete:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        WriteResult result = ds.delete(type, id);

        if (result.isUpdateOfExisting()) {
            Logger.debug("delete-success:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            Logger.debug("delete-failed to delete:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Override
    public void update(String id, T transientObject) {
        Logger.debug("update:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        ds.save(transientObject);
//        UpdateOperations<T> ops = new UpdateOperations();
//        UpdateResults r = ds.update(transientObject, ops);
//
//        if (r.getUpdatedCount() == 1) {
//            Logger.debug("update:success{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
//        } else {
//            Logger.error("update:failed to update{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
//        }
    }

    @Override
    public T get(String id) {
        Logger.debug("get-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        return ds.get(type, id);
    }

    @Override
    public void add(T newInstance) {
        Logger.debug("add:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        ds.save(newInstance);
    }
}
