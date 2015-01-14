/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.repository.impl;

import com.gemini.common.repository.impl.BaseRepositoryMongoDBImpl;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.repository.GeminiEnvironmentRepository;
import com.mongodb.MongoClient;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
public class GeminiEnvironmentRepositoryMongoDBImpl extends BaseRepositoryMongoDBImpl<GeminiEnvironment, String>
        implements GeminiEnvironmentRepository {

    public GeminiEnvironmentRepositoryMongoDBImpl(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(GeminiEnvironment.class, mongoClient, morphia, dbName);
    }

    public GeminiEnvironment getEnvByName(String envName) {
        Logger.debug("get app by name :{}", envName);
        return findOne(getDatastore().createQuery(GeminiEnvironment.class).filter("name", envName));
    }

    public List<GeminiApplication> getEnvApps(String envName) {
        Logger.debug("get env applications: {}", envName);
        return getEnvByName(envName).getApplications();
    }

    public List<GeminiNetwork> getEnvNetworks(String envName) {
        Logger.debug("get env networks: {}", envName);
        return getEnvByName(envName).getNetworks();
    }

    public List<GeminiServer> getEnvServers(String envName) {
        Logger.debug("get env servers: {}", envName);
        return getEnvByName(envName).getServers();
    }
}
