/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.domain.model.application;

import java.io.Serializable;

/**
 *
 * @author schari
 */
public class ApolloApplication implements Serializable {
    private String id;
    private String name;
    private String description;
    private String custom; //string for any custom description, URL's etc.
    private Integer backupSize;
    private String location; //TODO: convert to a geo coordinate 
//    private ArrayList<GeminiNetwork> networks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

//    public GeminiNetwork addNetwork(GeminiNetwork newNetwork) {
//        if (!networks.contains(newNetwork)) {
//            if (!networks.add(newNetwork)) //failed to add the network;
//            {
//                Logger.info("Failed to add network, start address: " + newNetwork.getStart().toString()
//                        + " end address: " + newNetwork.getEnd().toString());
//            }
//        } else {
//                Logger.info("Did not add network, network already exists. Start address: " + newNetwork.getStart().toString()
//                        + " end address: " + newNetwork.getEnd().toString());
//        }
//        return newNetwork;
//    }
//
//    public GeminiNetwork deleteNetwork(GeminiNetwork newNetwork) {
//        if (networks.contains(newNetwork)) {
//            if (!networks.remove(newNetwork)) {//failed to add the network;
//                Logger.info("Failed to remove network, start address: " + newNetwork.getStart().toString()
//                        + " end address: " + newNetwork.getEnd().toString());
//            } else {
//                Logger.info("Delete Network: network not found, start address: " + newNetwork.getStart().toString()
//                        + " end address: " + newNetwork.getEnd().toString());
//            }
//        }
//        return newNetwork;
//    }
//
//    public GeminiNetwork updateNetwork(GeminiNetwork newNetwork) {
//        if (networks.contains(newNetwork)) {
//            networks.add(newNetwork);
//            Logger.info("Updated Network: start address: " + newNetwork.getStart().toString()
//                    + " end address: " + newNetwork.getEnd().toString());
//            return newNetwork;
//        } else {
//            Logger.info("Could not update network: network not found, start address: " + newNetwork.getStart().toString()
//                    + " end address: " + newNetwork.getEnd().toString());
//            return null;
//        }
//    }
}
