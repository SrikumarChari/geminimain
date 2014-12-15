/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model;

import com.apollo.common.repository.EntityMongoDB;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Entity("ApolloApplication")
public class ApolloApplication extends EntityMongoDB {

    private String name;
    private String description;
    private String custom; //string for any custom description, URL's etc.
    private Integer backupSize;
    private String location; //TODO: convert to a geo coordinate 

    @Reference
    List<ApolloNetwork> networks;

    @Reference
    List<ApolloServer> servers;

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
            Logger.error("Did not add server:{}  already exists in application {}", s.getName(), getName());
        } else {
            if (!servers.add(s)) {
                Logger.debug("Failed to add server: {}", s.getName());
            } else {
                s.setApp(this);
                Logger.debug("Successfully added server: {} to application: ", s.getName(), getName());
            }
        }
    }

    public boolean deleteServer(ApolloServer s) {
        if (servers.contains(s)) {
            if (!servers.remove(s)) {
                Logger.debug("Failed to delete server: " + s.getName());
                return false;
            } else {
                //remove the connection between this application and the deleted server
                s.setApp(null);
                Logger.debug("Successfull deleted server: {}", s.getName());
                return true;
            }
        } else {
            Logger.error("Did not delete server: {} - server does not exist in application {}", s.getName(), getName());
            return false;
        }
    }

    public void addNetwork(ApolloNetwork n) {
        if (networks.contains(n)) {
            Logger.debug("Did not add network start: {} end: {}, already exists in application {}", n.getStart(), n.getEnd(), getName());
        } else {
            if (!networks.add(n)) {
                Logger.error("Failed to add network, start: {} end: {} from application {}", n.getStart(), n.getEnd(), getName());
            } else {
                n.setApp(this);
                Logger.debug("Successfully added network, start: {} end: {} to application {}", n.getStart(), n.getEnd(), getName());
            }
        }
    }

    public boolean deleteNetwork(ApolloNetwork n) {
        if (networks.contains(n)) {
            if (!networks.remove(n)) {
                Logger.error("Failed to delete network, start: {} end: {} from application {}", n.getStart(), n.getEnd(), getName());
                return false;
            } else {
                //remove the connection between this application and the deleted network
                n.setApp(null);
                Logger.debug("Successfully deleted network, start: {} end: {} from application {}", n.getStart(), n.getEnd(), getName());
                return true;
            }
        } else {
            Logger.debug("Did not delete network, start: {} end: {} - network does not exist in application {}", n.getStart(), n.getEnd(), getName());
            return false;
        }
    }

}
