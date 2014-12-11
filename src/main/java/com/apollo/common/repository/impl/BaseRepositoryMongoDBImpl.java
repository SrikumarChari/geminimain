/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.common.repository.impl;

import com.apollo.common.repository.BaseRepository;
import com.google.gson.Gson;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.*;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 *
 * @author schari
 * @param <T>
 * @param <PK>
 */
public abstract class BaseRepositoryMongoDBImpl<T, PK extends Serializable>
        implements BaseRepository<T, PK> {

    private Class<T> type;
    private Class<PK> idType;

    private final DB db;
    private final DBCollection collection;

    private static final Logger logger = LoggerFactory
            .getLogger(BaseRepositoryMongoDBImpl.class);

    public BaseRepositoryMongoDBImpl(Class<T> type, Class<PK> idType, DB db) {
        this.type = type;
        this.idType = idType;
        this.db = db;
        this.collection = db.getCollection(type.getSimpleName());
    }

    protected DBCollection getCollection() {
        return collection;
    }

    protected DB getDB() {
        return db;
    }

    public Class<T> getType() {
        return type;
    }

    public Class<PK> getId() {
        return idType;
    }

    @Override
    public List<T> list() {
        List<T> objList = new ArrayList<>();

        logger.debug("list-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        DBCursor dbObjects = collection.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            Gson g = new Gson();
            T anObj = g.fromJson(JSON.serialize(dbObject), type);
            objList.add(anObj);
        }
        return objList;
    }

    @Override
    public void delete(PK id) {
        //find the record and remove.
        logger.debug("delete-find:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        BulkWriteOperation builder = collection.initializeOrderedBulkOperation();
        builder.find(new BasicDBObject("_id", id)).removeOne();

        //execute (or flush in SQL)
        logger.debug("delete-execute:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        BulkWriteResult result = builder.execute();

        //check for success
        if (result.getRemovedCount() != 1) {
            logger.error("delete-execute:{} - failed to delete", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            logger.debug("delete-execute:{} - sucessfully deleted", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Override
    public void update(T transientObject) {
        //build the db object
        logger.debug("update-build replacement:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        Gson g = new Gson();
        DBObject dbObject = (DBObject) JSON.parse(g.toJson(transientObject, type));

        //find the record and remove.
        logger.debug("update-find:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        BulkWriteOperation builder = getCollection().initializeOrderedBulkOperation();
        builder.find(dbObject).replaceOne(dbObject);

        //execute (or flush in SQL)
        logger.debug("update-execute:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        BulkWriteResult result = builder.execute();

        //check for success, mongo recommends that we see if the modified count is available in the first place
        if (result.isModifiedCountAvailable()) {
            if (result.getModifiedCount() != 1) {
                logger.error("update-execute:{} - failed to update", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
            } else {
                logger.debug("update-execute:{} - sucessfully deleted", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
            }
        } else {
            logger.debug("update-execute:{} - could not verify update", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Override
    public T get(PK id) {
        logger.debug("get-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        DBObject dbObject = collection.findOne(new BasicDBObject("_id", id));

        if (dbObject == null) {
            logger.debug("get-object not found:{}" + id, ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        } else {
            logger.debug("get-object found:{}" + id, ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        }
        return type.cast(dbObject);
    }

    @Override
    public void add(T newInstance) {
        //build the db object
        logger.debug("add-build DBObject:{}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        Gson g = new Gson();
        DBObject dbObject = (DBObject) JSON.parse(g.toJson(newInstance, type));

        logger.debug("add-insert:{}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        WriteResult result = collection.insert(dbObject);
        if (result.isUpdateOfExisting()) {
            logger.debug("add-insert-sucess:{}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            logger.error("add-insert-error:{}", ToStringBuilder.reflectionToString(newInstance, ToStringStyle.MULTI_LINE_STYLE));
        }
        
        //Object retID = dbObject.get("_id");
    }
}
