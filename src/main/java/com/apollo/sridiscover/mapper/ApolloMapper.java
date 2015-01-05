/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover.mapper;

import com.apollo.domain.dto.ApolloApplicationDTO;
import com.apollo.domain.dto.ApolloNetworkDTO;
import com.apollo.domain.dto.ApolloServerDTO;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.model.ApolloNetwork;
import com.apollo.domain.model.ApolloServer;
import com.google.inject.Inject;
import org.dozer.DozerBeanMapper;

/**
 *
 * @author schari
 */
public class ApolloMapper {

    @Inject
    private DozerBeanMapper mapper;

    public ApolloApplication getAppFromDTO(ApolloApplicationDTO appDTO) {
        ApolloApplication newApp = mapper.map(appDTO, ApolloApplication.class);
        return newApp;
    }

    public ApolloApplicationDTO getDTOFromApp(ApolloApplication app) {
        ApolloApplicationDTO newAppDTO = mapper.map(app, ApolloApplicationDTO.class);
        return newAppDTO;
    }

    public ApolloNetwork getNetworkFromDTO(ApolloNetworkDTO netDTO) {
        ApolloNetwork net = mapper.map(netDTO, ApolloNetwork.class);
        return net;
    }

    public ApolloNetworkDTO getDTOFromNetwork(ApolloNetwork net) {
        ApolloNetworkDTO netDTO = mapper.map(net, ApolloNetworkDTO.class);
        return netDTO;
    }

    public ApolloServer getServerFromDTO(ApolloServerDTO srvDTO) {
        ApolloServer srv = mapper.map(srvDTO, ApolloServer.class);
        return srv;
    }

    public ApolloServerDTO getDTOFromServer(ApolloServer srv) {
        ApolloServerDTO srvDTO = mapper.map(srv, ApolloServerDTO.class);
        return srvDTO;
    }
}
