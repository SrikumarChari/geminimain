/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover;

import java.net.InetAddress;

/**
 *
 * @author schari
 */
public class ApolloNetwork {
    private String id;
    private String appID; 
    private InetAddress start;
    private InetAddress end;
    private String networkType;
    //private ArrayList<GeminiServer> activeDevices;
    //private List<String> vlans; //for now it is a string, need to convert to InetAddress later

    public ApolloNetwork() {
        this.id = "";
        this.appID = "";
        this.networkType = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
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

//    public List<GeminiServer> getActiveDevices() {
//        return Collections.unmodifiableList(activeDevices);
//    }

//    public void setActiveDevices(ArrayList<GeminiServer> activeDevices) {
//        this.activeDevices = activeDevices;
//    }

//    public void addActiveDevice(GeminiServer d) {
//        if (activeDevices.contains(d)) {
//            Logger.info("Did not add server: " + d.getName() + " Server already exists.");
//        } else {
//            if (!activeDevices.add(d)) {
//                Logger.info("Failed to add server: " + d.getName());
//            }
//        }
//    }
//
//    public boolean deleteActiveDevice(GeminiServer d) {
//        if (activeDevices.contains(d)) {
//            if (!activeDevices.remove(d)) {
//                Logger.info("Failed to delete server: " + d.getName());
//                return false;
//            }
//            return true;
//        } else {
//            Logger.info("Did not delete server: " + d.getName() + " Server does not exist.");
//            return false;
//
//        }
//    }
//
//    public List<String> getVlans() {
//        return vlans;
//    }
//
//    public void setVlans(List<String> vlans) {
//        this.vlans = vlans;
//    }
}
