/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover;

import com.apollo.domain.model.ApolloServer;
import com.apollo.domain.model.ApolloNetwork;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.repository.impl.ApolloApplicationRepositoryMongoDBImpl;
import com.apollo.domain.repository.impl.ApolloNetworkRepositoryMongoDBImpl;
import com.apollo.domain.repository.impl.ApolloServerRepositoryMongoDBImpl;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.net.InetAddresses;
import com.mongodb.MongoClient;
import java.util.ArrayList;
import static spark.Spark.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.Level;
import spark.Request;
import spark.Response;

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
    static MongoClient mongoClient;
    static Morphia morphia = new Morphia();
    static Datastore ds;

    public static void main(String[] args) throws IOException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        //create table for the application, networks and servers
        morphia.map(ApolloApplication.class).map(ApolloNetwork.class).map(ApolloServer.class);
        ds = morphia.createDatastore(mongoClient, "Apollo");

        //set the current logging level to debug
        Configurator.currentConfig().level(Level.DEBUG).activate();
        
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
            try {
                List<ApolloApplication> apps = getApplicationsFromDB();
                if (apps == null) {
                    return "No Applications";
                } else {
                    return apps;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}\n", "localhost");
                return "Severe Error: Unknown host";
            }
            //return applications;
        }, new JsonTransformer());

        //return application with ID = ':id'
        get("/applications/:name", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String appName = request.params(":name");
            response.status(404);
            return "Application with ID: " + appName + " not found!";
        }, new JsonTransformer());

        //return all networks related to application with ID = ':id'
        get("/applications/:name/networks", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String appID = request.params(":name");
//            for (ApolloApplication a : applications) {
//                if (a.getId().equals(appID)) {
//                    List<ApolloNetwork> retNetworks = new ArrayList<>();
//                    for (ApolloNetwork n : networks) {
//                        if (n.getAppID().equals(appID)) {
//                            retNetworks.add(n);
//                        }
//                    }
//                    return retNetworks;
//                }
//            }
            response.status(404);
            return "Application with ID: " + appID + " not found!";
        }, new JsonTransformer());

        //return all servers related to application with ID = ':id'
        get("/applications/:id/servers", "application/json", (Request request, Response response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String appID = request.params(":id");
//            for (ApolloApplication a : applications) {
//                if (a.getId().equals(appID)) {
//                    List<ApolloServer> retServers = new ArrayList<>();
//                    for (ApolloServer s : servers) {
//                        if (s.getAppID().equals(appID)) {
//                            retServers.add(s);
//                        }
//                    }
//                    return retServers;
//                }
//            }
            response.status(404);
            return "Application with ID: " + appID + " not found!";
        }, new JsonTransformer());

        //return all servers related to application with ID = ':appID' AND network with ID = ':nID'
        get("/applications/:appID/networks/:nID/servers", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String appID = request.params(":appID");
            String nID = request.params(":nID");
//            for (ApolloApplication a : applications) {
//                if (a.getId().equals(appID)) {
//                    List<ApolloServer> retServers = new ArrayList<>();
//                    for (ApolloNetwork n : networks) {
//                        if (n.getId().equals(nID)) {
//                            for (ApolloServer s : servers) {
//                                if (s.getNetworkID().equals(nID) && s.getAppID().equals(appID)) {
//                                    retServers.add(s);
//                                }
//                            }
//                        }
//                    }
//                    return retServers;
//                }
//            }
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
                if (n.getId().equals(nID)) {
                    return n;
                }
            }
            response.status(404);
            return "Network with ID: " + nID + " not found!";
        }, new JsonTransformer());

        get("/servers", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            return servers;
        }, new JsonTransformer());

        get("/servers/:id", "application/json", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            String sID = request.params(":id");
            for (ApolloServer s : servers) {
                if (s.getId().equals(sID)) {
                    return s;
                }
            }
            response.status(404);
            return "Server with ID: " + sID + " not found!";
        }, new JsonTransformer());

        //close the db client
        //mongoClient.close();
    }

    private static void createSampleData() throws UnknownHostException {
        //setup the random generator and seeds
        Integer serverMin = 10000000, serverMax = 20000000;
        Integer networkMin = 30000000, networkMax = 40000000;
        Integer appMin = 50000000, appMax = 60000000;
        int numServers = 40, numNetworks = 5, numApps = 2;

        //for now use a clean all the tables before generating the data
        ds.getCollection(ApolloApplication.class).drop();
        ds.getCollection(ApolloNetwork.class).drop();
        ds.getCollection(ApolloServer.class).drop();

        //create the application repository object
        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(ds);
        ApolloNetworkRepositoryMongoDBImpl netDB = new ApolloNetworkRepositoryMongoDBImpl(ds);
        ApolloServerRepositoryMongoDBImpl srvDB = new ApolloServerRepositoryMongoDBImpl(ds);

        //first the server objects
        Random sRand = new Random();
        for (int i = 0; i < numServers; i++) {
            String serverID = String.valueOf(sRand.nextInt((serverMax - serverMin) + 1) + serverMin);
            ApolloServer s = new ApolloServer();
            s.setName(serverID + "name");
            s.setDescription(serverID + " description");
            s.setAddress(InetAddresses.forString(InetAddresses.fromInteger(Integer.parseInt(serverID)).getHostAddress()));
            s.setSubnetMask("255.255.255.0");
            s.setPort(443);
            s.setOs("Linux");
            s.setManufacturer(serverID + " manu");
            s.setBackupSize(Integer.parseInt(serverID) + 10);
            s.setLocation(serverID + " locker");
            s.setAdmin(serverID + " admin");
            s.setPassword(serverID + " password");
            srvDB.add(s);
            servers.add(s);
        }

        //next create network objects
        Random nRand = new Random();
        for (int i = 0; i < numNetworks; i++) {
            String networkID = String.valueOf(nRand.nextInt((networkMax - networkMin) + 1) + networkMin);
            ApolloNetwork n = new ApolloNetwork();
            n.setNetworkType("Class C");
            n.setStart(InetAddresses.forString(InetAddresses.fromInteger(Integer.parseInt(networkID)).getHostAddress()));
            n.setEnd(InetAddresses.increment(n.getStart()));

            //add 'numServer' divided by 'numNetwork' servers from the servers array to each network
            for (int start = i * (numServers / numNetworks), j = 0; j < (numServers / numNetworks) && (start + j) < numServers; j++) {
                n.addServer(servers.get(start + j));
            }
            netDB.add(n);
            networks.add(n);
        }

        //next create the application objects
        Random aRand = new Random();
        for (int i = 0; i < numApps; i++) {
            String appID = String.valueOf(aRand.nextInt((appMax - appMin) + 1) + appMin);
            ApolloApplication a = new ApolloApplication();
            //a.setId(appID.toString());
            a.setName(appID + " name");
            a.setDescription(appID + " description");
            a.setCustom(appID + " custom");
            a.setLocation(appID + " location");
            a.setBackupSize(500);

            //add 'numNetworks' divided by 'numApps' networks from the networks array to each applicaiton
            for (int start = i * (numNetworks / numApps), j = 0; j < (numNetworks / numApps) && (start + j) < numNetworks; j++) {
                a.addNetwork(networks.get(start + j));
            }

            //add 'numServers' divided by 'numApps' servers from the servers array to each applicaiton
            for (int start = i * (numServers / numApps), j = 0; j < (numServers / numApps) && (start + j) < numServers; j++) {
                a.addServer(servers.get(start + j));
            }

            //add to database
            applications.add(a);
            appDB.add(a);
        }

        //just change the name of one of the application objects in the database
        ApolloApplication a = applications.get(0);
        a.setName("Srikumar Chari");
        appDB.update(a.getId().toString(), a);

        //get the second application
        a = appDB.getAppByName(a.getName());

        List<ApolloApplication> lApps = appDB.list();
        List<ApolloNetwork> lNets = netDB.list();
        List<ApolloServer> lSrvs = srvDB.list();
    }

    private static List<ApolloApplication> getApplicationsFromDB() throws UnknownHostException {
        //morphia.map(ApolloApplication.class);
        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(ds);
        List<ApolloApplication> l = appDB.list();
        return l;
    }
}
