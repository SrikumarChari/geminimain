/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover;

import com.apollo.domain.model.application.ApolloApplication;
import com.google.common.net.InetAddresses;
import java.util.ArrayList;
import static spark.Spark.*;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author schari
 *
 * TODO: Need to integrate DROPWIZARD METRICS
 *
 */
public class SriDiscover {

    static ArrayList<ApolloApplication> applications = new ArrayList<>();
    static ArrayList<ApolloServer> servers = new ArrayList<>();
    static ArrayList<ApolloNetwork> networks = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //create some data to transfer to the front end
        createSampleData();

        //return all applications
        get("/applications", "application/json", (request, response) -> {
            //set the CORS filters...
            response.header("Access-Control-Allow-Origin", "*");
            //response.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            //response.header("Access-Control-Max-Age", "3600");
            //response.header("Access-Control-Allow-Headers", "x-requested-with");
            response.status(200);
            return applications;
        }, new JsonTransformer());

        //return application with ID = ':id'
        get("/applications/:id", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String appID = request.params(":id");
            for (ApolloApplication a : applications) {
                if (a.getId().equals(Integer.parseInt(appID))) {
                    return a;
                }
            }
            response.status(404);
            return "Application with ID: " + Integer.parseInt(appID) + " not found!";
        }, new JsonTransformer());

        //return all networks related to application with ID = ':id'
        get("/applications/:id/networks", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            Integer appID = Integer.parseInt(request.params(":id"));
            for (ApolloApplication a : applications) {
                if (a.getId().equals(appID)) {
                    List<ApolloNetwork> retNetworks = new ArrayList<>();
                    for (ApolloNetwork n : networks) {
                        if (n.getAppID().equals(appID)) {
                            retNetworks.add(n);
                        }
                    }
                    return retNetworks;
                }
            }
            response.status(404);
            return "Application with ID: " + appID + " not found!";
        }, new JsonTransformer());

        //return all servers related to application with ID = ':id'
        get("/applications/:id/servers", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            Integer appID = Integer.parseInt(request.params(":id"));
            for (ApolloApplication a : applications) {
                if (a.getId().equals(appID)) {
                    List<ApolloServer> retServers = new ArrayList<>();
                    for (ApolloServer s : servers) {
                        if (s.getAppID().equals(appID)) {
                            retServers.add(s);
                        }
                    }
                    return retServers;
                }
            }
            response.status(404);
            return "Application with ID: " + appID + " not found!";
        }, new JsonTransformer());

        //return all servers related to application with ID = ':appID' AND network with ID = ':nID'
        get("/applications/:appID/networks/:nID/servers", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            Integer appID = Integer.parseInt(request.params(":appID"));
            Integer nID = Integer.parseInt(request.params(":nID"));
            for (ApolloApplication a : applications) {
                if (a.getId().equals(appID)) {
                    List<ApolloServer> retServers = new ArrayList<>();
                    for (ApolloNetwork n : networks) {
                        if (n.getId().equals(nID)) {
                            for (ApolloServer s : servers) {
                                if (s.getNetworkID().equals(nID) && s.getAppID().equals(appID)) {
                                    retServers.add(s);
                                }
                            }
                        }
                    }
                    return retServers;
                }
            }
            response.status(404);
            return "Application with ID: " + appID + " not found!";
        }, new JsonTransformer());

        get("/networks", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            return networks;
        }, new JsonTransformer());

        get("/networks/:id", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String nID = request.params(":id");
            for (ApolloNetwork n : networks) {
                if (n.getId().equals(Integer.parseInt(nID))) {
                    return n;
                }
            }
            response.status(404);
            return "Network with ID: " + Integer.parseInt(nID) + " not found!";
        }, new JsonTransformer());

        get("/servers", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            return servers;
        }, new JsonTransformer());

        get("/servers/:id", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String sID = request.params(":id");
            for (ApolloServer s : servers) {
                if (s.getId().equals(Integer.parseInt(sID))) {
                    return s;
                }
            }
            response.status(404);
            return "Server with ID: " + Integer.parseInt(sID) + " not found!";
        }, new JsonTransformer());

    }

    private static void createSampleData() {
        //setup the random generator and seeds
        Integer serverMin = 10000000, serverMax = 20000000;
        Integer networkMin = 30000000, networkMax = 40000000;
        Integer appMin = 50000000, appMax = 60000000;
        int numServers = 50000, numNetworks = 1000, numApps = 100;

        //first the server objects
        Random sRand = new Random();
        for (int i = 0; i < numServers; i++) {
            int serverID = sRand.nextInt((serverMax - serverMin) + 1) + serverMin;
            ApolloServer s = new ApolloServer();
            s.setId(serverID);
            s.setName(serverID + "name");
            s.setDescription(serverID + " description");
            s.setAddress(InetAddresses.forString(InetAddresses.fromInteger(serverID).getHostAddress()));
            String tmp = s.getAddress().toString();
            s.setSubnetMask("255.255.255.0");
            s.setPort(443);
            s.setManufacturer(serverID + " manu");
            s.setBackupSize(serverID + 10);
            s.setLocation(serverID + " locker");
            s.setAdmin(serverID + " admin");
            s.setPassword(serverID + " password");
            servers.add(s);
        }

        //next create network objects
        Random nRand = new Random();
        for (int i = 0; i < numNetworks; i++) {
            int networkID = nRand.nextInt((networkMax - networkMin) + 1) + networkMin;
            ApolloNetwork n = new ApolloNetwork();
            n.setId(networkID);
            n.setNetworkType("Class C");
            n.setStart(InetAddresses.forString(InetAddresses.fromInteger(networkID).getHostAddress()));
            n.setEnd(InetAddresses.increment(n.getStart()));

            //add 'numServer' divided by 'numNetwork' servers from the servers array to each network
            for (int start = i * (numServers / numNetworks), j = 0; j < (numServers / numNetworks) && (start + j) < numServers; j++) {
                servers.get(start + j).setNetworkID(networkID);
            }
            networks.add(n);
        }

        //next create the application objects
        Random aRand = new Random();
        for (int i = 0; i < numApps; i++) {
            int appID = aRand.nextInt((appMax - appMin) + 1) + appMin;
            ApolloApplication a = new ApolloApplication();
            a.setId(appID);
            a.setName(appID + " name");
            a.setDescription(appID + " description");
            a.setCustom(appID + " custom");
            a.setLocation(appID + " location");
            a.setBackupSize(500);

            //add 'numNetworks' divided by 'numApps' networks from the networks array to each applicaiton
            for (int start = i * (numNetworks / numApps), j = 0; j < (numNetworks / numApps) && (start + j) < numNetworks; j++) {
                networks.get(start + j).setAppID(appID);
            }

            //add 'numServers' divided by 'numApps' servers from the servers array to each applicaiton
            for (int start = i * (numServers / numApps), j = 0; j < (numServers / numApps) && (start + j) < numServers; j++) {
                servers.get(start + j).setAppID(appID);
            }

            applications.add(a);
        }
    }
}
