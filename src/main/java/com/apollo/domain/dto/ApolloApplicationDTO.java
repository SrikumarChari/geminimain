/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author schari
 */
public class ApolloApplicationDTO extends BaseDTO {

    private String name;
    private String description;
    private String custom; //string for any custom description, URL's etc.
    private Integer backupSize;
    private String location; //TODO: convert to a geo coordinate 

    private List<ApolloNetworkDTO> networks;
    private List<ApolloServerDTO> servers;

    public ApolloApplicationDTO() {
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

    public List<ApolloNetworkDTO> getNetworks() {
        return networks;
    }
    
    public void setNetworks(List<ApolloNetworkDTO> n) {
        networks = n;
    }

    public List<ApolloServerDTO> getServers() {
        return servers;
    }
    
    public void setServers(List<ApolloServerDTO> s) {
        servers = s;
    }
}
