/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model;

import com.apollo.common.repository.EntityMongoDB;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Entity
public class ApolloNetwork extends EntityMongoDB {

    private InetAddress start;
    private InetAddress end;
    private String networkType;

//    @Reference
//    private ApolloApplication app;

    @Reference
    List<ApolloServer> servers;

    public ApolloNetwork() {
        this.networkType = "";
        servers = new ArrayList();
    }

    public InetAddress getStart() {
        return start;
    }

    public void setStart(InetAddress start) {
        this.start = start;
    }

    public InetAddress getEnd() {
        return end;
    }

    public void setEnd(InetAddress end) {
        this.end = end;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

//    public ApolloApplication getApp() {
//        return app;
//    }
//
//    public void setApp(ApolloApplication app) {
//        this.app = app;
//    }

    public void addServer(ApolloServer s) {
        if (servers.contains(s)) {
            Logger.error("Did not add server:{}  already exists in network with start: {} and end: {}", s.getName(), start, end);
        } else {
            if (!servers.add(s)) {
                Logger.info("Failed to add server: " + s.getName());
            } else {
                //add a connection between this network and the server
                Logger.info("Successfully added server: {}", s.getName());
                //s.setNetwork(this);
            }
        }
    }

    public boolean deleteServer(ApolloServer s) {
        if (servers.contains(s)) {
            if (!servers.remove(s)) {
                Logger.error("Failed to delete server: " + s.getName());
                return false;
            } else {
                //remove the connection between this network and the server
                //s.setNetwork(null);
                Logger.info("Successfully deleted server: {}", s.getName());
                return true;
            }
        } else {
            Logger.error("Did not delete server: {} - server does not exist", s.getName());
            return false;
        }
    }
}
