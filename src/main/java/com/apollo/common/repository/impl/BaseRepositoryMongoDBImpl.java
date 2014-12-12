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
public abstract class BaseRepositoryMongoDBImpl<T, String>
        implements BaseRepository<T, String> {

    private Class<T> type;

    private final DB db;
    private final DBCollection collection;

    private static final Logger logger = LoggerFactory
            .getLogger(BaseRepositoryMongoDBImpl.class);

    public BaseRepositoryMongoDBImpl(Class<T> type, DB db) {
        this.type = type;
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
    public void delete(String id) {
        //build query with id.
        logger.debug("delete-build query:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        BasicDBObject query = null;
        try {
            query = new BasicDBObject("_id", new ObjectId((java.lang.String) id));
        } catch (IllegalArgumentException i) {
            logger.debug("delete-build query-invalid id:{}", ToStringBuilder.reflectionToString(id + "\n" + i.toString(), ToStringStyle.MULTI_LINE_STYLE));
            return;
        }

        //find the object
        logger.debug("delete-find:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        DBObject dbObject = getCollection().findOne(query);
        if (dbObject == null) {
            logger.error("delete-find:{} - could not find object.", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
            return;
        }

        //execute
        logger.debug("delete-execute:{}", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        WriteResult result = getCollection().remove(dbObject);

        //check for success
        if (result.isUpdateOfExisting()) {
            logger.debug("delete-execute:{} - sucessfully deleted", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            logger.error("delete-execute:{} - failed to delete", ToStringBuilder.reflectionToString(id, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Override
    public void update(String id, T transientObject) {
        //build the db object
        logger.debug("update-build replacement:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        Gson g = new Gson();
        BasicDBObject query = new BasicDBObject("_id", new ObjectId((java.lang.String) id));

        //find the record and remove.
        logger.debug("update-find:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        DBObject existingObj = getCollection().findOne(query);
        if (existingObj == null) {
            logger.error("update-find:{} - could not find object.", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
            return;
        }

        //save the object
        logger.debug("update-execute:{}", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        DBObject newDBObject = (DBObject) JSON.parse(g.toJson(transientObject, type));
        existingObj = newDBObject;
        WriteResult result = getCollection().save(existingObj);

        //check for success, mongo recommends that we see if the modified count is available in the first place
        if (result.isUpdateOfExisting()) {
            logger.debug("update-execute:{} - sucessfully updated", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            logger.error("update-execute:{} - failed to update", ToStringBuilder.reflectionToString(transientObject, ToStringStyle.MULTI_LINE_STYLE));
        }
    }

    @Override
    public T get(String id) {
        logger.debug("get-find:{}", ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        DBObject dbObject = collection.findOne(new BasicDBObject("_id", new ObjectId((java.lang.String) id)));

        if (dbObject == null) {
            logger.debug("get-object not found:{}" + id, ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
            return null;
        } else {
            logger.debug("get-object found:{}" + id, ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        }

        logger.debug("get-object serialize:{}" + id, ToStringBuilder.reflectionToString(type.getSimpleName(), ToStringStyle.MULTI_LINE_STYLE));
        Gson g = new Gson();
        return g.fromJson(JSON.serialize(dbObject), type);
    }

    @Override
    public String add(T newInstance) {
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
        return (String) dbObject.get("_id").toString();
    }
}
