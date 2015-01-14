/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.mapper;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.dto.GeminiTenantDTO;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.tenant.GeminiTenant;
import com.google.inject.Inject;
import org.dozer.DozerBeanMapper;

/**
 *
 * @author schari
 */
public class GeminiMapper {

    @Inject
    private DozerBeanMapper mapper;

    public GeminiApplication getAppFromDTO(GeminiApplicationDTO appDTO) {
        GeminiApplication newApp = mapper.map(appDTO, GeminiApplication.class);
        return newApp;
    }

    public GeminiApplicationDTO getDTOFromApp(GeminiApplication app) {
        GeminiApplicationDTO newAppDTO = mapper.map(app, GeminiApplicationDTO.class);
        return newAppDTO;
    }

    public GeminiNetwork getNetworkFromDTO(GeminiNetworkDTO netDTO) {
        GeminiNetwork net = mapper.map(netDTO, GeminiNetwork.class);
        return net;
    }

    public GeminiNetworkDTO getDTOFromNetwork(GeminiNetwork net) {
        GeminiNetworkDTO netDTO = mapper.map(net, GeminiNetworkDTO.class);
        return netDTO;
    }

    public GeminiServer getServerFromDTO(GeminiServerDTO srvDTO) {
        GeminiServer srv = mapper.map(srvDTO, GeminiServer.class);
        return srv;
    }

    public GeminiServerDTO getDTOFromServer(GeminiServer srv) {
        GeminiServerDTO srvDTO = mapper.map(srv, GeminiServerDTO.class);
        return srvDTO;
    }
    
    public GeminiEnvironment getEnvFromDTO (GeminiEnvironmentDTO envDTO) {
        GeminiEnvironment env = mapper.map(envDTO, GeminiEnvironment.class);
        return env;
    }
    public GeminiEnvironmentDTO getDTOFromEnv(GeminiEnvironment env) {
        GeminiEnvironmentDTO envDTO = mapper.map(env, GeminiEnvironmentDTO.class);
        return envDTO;        
    }

    public GeminiTenant getTenantFromDTO (GeminiTenantDTO tenantDTO) {
        GeminiTenant tenant = mapper.map(tenantDTO, GeminiTenant.class);
        return tenant;
    }
    public GeminiTenantDTO getDTOFromEnv(GeminiTenant tenant) {
        GeminiTenantDTO tenantDTO = mapper.map(tenant, GeminiTenantDTO.class);
        return tenantDTO;        
    }
}
