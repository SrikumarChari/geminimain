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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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

    public void addServer(ApolloServer s) {
        if (servers.contains(s)) {
            Logger.info("Did not add server:{} already exists in network {}",
                    ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
        } else {
            if (!servers.add(s)) {
                Logger.error("Failed to add server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
            } else {
                //add a connection between this network and the server
                Logger.debug("Successfully added server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                //s.setNetwork(this);
            }
        }
    }

    public boolean deleteServer(ApolloServer s) {
        if (servers.contains(s)) {
            if (!servers.remove(s)) {
                Logger.error("Failed to delete server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                return false;
            } else {
                //remove the connection between this network and the server
                //s.setNetwork(null);
                Logger.debug("Successfully deleted server: {}",
                        ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE));
                return true;
            }
        } else {
            Logger.info("Did not delete server, server does not exist in networkserver: {} network: {}",
                    ToStringBuilder.reflectionToString(s, ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
            return false;
        }
    }

    public List<ApolloServer> getServers() {
        Logger.debug("Network getServers: {}",
                ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE));
        return servers;
    }
}
