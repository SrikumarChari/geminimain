/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model;

import com.apollo.common.repository.EntityMongoDB;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class ApolloServer extends EntityMongoDB {
//    private String networkID; //network to which this server belongs to
//    private String appID; //application to which this server belongs to
    private String name;
    private String description;
    private InetAddress address;
    private String subnetMask;
    private Integer port;
    private String os;
    private String type; //TODO: Convert to an enum when the types are finalized
    private String manufacturer;
    private Integer backupSize = 0;
    private String location; //TODO: convert to geo coordinates later 
    private String admin;
    private String password;

//    @Reference
//    private ApolloApplication app; // application to which this server belongs to
//    
//    @Reference
//    private ApolloNetwork network; //network to which this server belongs to

    public ApolloServer() {
//        app = new ApolloApplication();
//        network = new ApolloNetwork();
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

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public InetAddress getAddress() {
        return address;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public ApolloApplication getApp() {
//        return app;
//    }
//
//    public void setApp(ApolloApplication app) {
//        this.app = app;
//    }
//
//    public ApolloNetwork getNetwork() {
//        return network;
//    }
//
//    public void setNetwork(ApolloNetwork network) {
//        this.network = network;
//    }
}
