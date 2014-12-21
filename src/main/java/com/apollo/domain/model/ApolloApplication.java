/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model;

import com.apollo.common.repository.EntityMongoDB;
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
public class ApolloApplication extends EntityMongoDB {

    private String name;
    private String description;
    private String custom; //string for any custom description, URL's etc.
    private Integer backupSize;
    private String location; //TODO: convert to a geo coordinate 

    @Reference
    private final List<ApolloNetwork> networks;

    @Reference
    private final List<ApolloServer> servers;

    public ApolloApplication() {
        networks = new ArrayList();
        servers = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public Integer getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(Integer backupSize) {
        this.backupSize = backupSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addServer(ApolloServer s) {
        if (servers.contains(s)) {
            Logger.info("Did not add server:{}  already exists in application {}",
                    ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
        } else {
            if (!servers.add(s)) {
                Logger.debug("Failed to add server: {}",
                        ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE));
            } else {
                //s.setApp(this);
                Logger.debug("Successfully added server: {} to application: {}",
                        ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
            }
        }
    }

    public boolean deleteServer(ApolloServer s) {
        if (servers.contains(s)) {
            if (!servers.remove(s)) {
                Logger.error("Failed to delete server: {}",
                        ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE));
                return false;
            } else {
                //remove the connection between this application and the deleted server
                //s.setApp(null);
                Logger.debug("Successfull deleted server: {}",
                        ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE));
                return true;
            }
        } else {
            Logger.error("Did not delete server: {} - server does not exist in application {}",
                    ToStringBuilder.reflectionToString(s.getName(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
            return false;
        }
    }

    public void addNetwork(ApolloNetwork n) {
        if (networks.contains(n)) {
            Logger.error("Did not add network start: {} end: {}, already exists in application {}",
                    ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
        } else {
            if (!networks.add(n)) {
                Logger.error("Failed to add network, start: {} end: {} from application {}",
                        ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
            } else {
                //n.setApp(this);
                Logger.debug("Successfully added network, start: {} end: {} to application {}",
                        ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
            }
        }
    }

    public boolean deleteNetwork(ApolloNetwork n) {
        if (networks.contains(n)) {
            if (!networks.remove(n)) {
                Logger.error("Failed to delete network, start: {} end: {} from application {}",
                        ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
                return false;
            } else {
                //remove the connection between this application and the deleted network
                //n.setApp(null);
                Logger.debug("Successfully deleted network, start: {} end: {} from application {}",
                        ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                        ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
                return true;
            }
        } else {
            Logger.info("Did not delete network, start: {} end: {} - network does not exist in application {}",
                    ToStringBuilder.reflectionToString(n.getStart(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(n.getEnd(), ToStringStyle.MULTI_LINE_STYLE),
                    ToStringBuilder.reflectionToString(getName(), ToStringStyle.MULTI_LINE_STYLE));
            return false;
        }
    }

    public List<ApolloNetwork> getNetworks() {
        Logger.debug("getNetworks");
        return networks;
    }

    public List<ApolloServer> getServers() {
        Logger.debug("getServers");
        return servers;
    }

}
