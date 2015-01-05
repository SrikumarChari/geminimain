/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.sridiscover;

import com.apollo.domain.dto.ApolloApplicationDTO;
import com.apollo.domain.dto.ApolloNetworkDTO;
import com.apollo.domain.dto.ApolloServerDTO;
import com.apollo.domain.model.ApolloServer;
import com.apollo.domain.model.ApolloNetwork;
import com.apollo.domain.model.ApolloApplication;
import com.apollo.domain.repository.impl.ApolloApplicationRepositoryMongoDBImpl;
import com.apollo.domain.repository.impl.ApolloNetworkRepositoryMongoDBImpl;
import com.apollo.domain.repository.impl.ApolloServerRepositoryMongoDBImpl;
import com.apollo.sridiscover.mapper.ApolloMapper;
import com.apollo.sridiscover.mapper.ApolloMapperModule;
import com.google.common.net.InetAddresses;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import java.util.ArrayList;
import static spark.Spark.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
        ds = morphia.createDatastore(mongoClient, "Apollo");

        //create the mapper
        Injector injector = Guice.createInjector(new ApolloMapperModule());
        ApolloMapper mapper = injector.getInstance(ApolloMapper.class);

        //set the current logging level to debug
        Configurator.currentConfig().level(Level.INFO).activate();

        //create some data to transfer to the front end
        createSampleData();

        //close the db client
        mongoClient.close();

        //check if authenticated, create the call context and user context here
        //for now it is empty!!!!
        before((request, response) -> {
            boolean authenticated = true;
            // ... check if authenticated
            if (!authenticated) {
                halt(401, "Nice try, you are not welcome here");
            }
        });

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            //response.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            //response.header("Access-Control-Max-Age", "3600");
            //response.header("Access-Control-Allow-Headers", "x-requested-with");
        });

        //return all applications
        get("/applications", "application/json", (request, response) -> {
            //set the CORS filters...
            try {
                List<ApolloApplication> apps = getApplicationsFromDB();
                if (apps == null || apps.isEmpty()) {
                    response.status(404);
                    Logger.info("Could not find any applications.");
                    return "No Applications found.";
                } else {
                    response.status(200);
                    Logger.debug("Found applications");
                    List<ApolloApplicationDTO> dtoApps = new ArrayList();
                    for (ApolloApplication a : apps) {
                        dtoApps.add(mapper.getDTOFromApp(a));
                    }
                    return dtoApps;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", "localhost");
                response.status(500);
                return "Severe Error: Unknown database host";
            }
        }, new JsonTransformer());

        //return application with ID = ':id'
        get("/applications/:name", "application/json", (request, response) -> {
            String appName;
            //decode the URL as it may contain escape characters, etc.
            try {
                appName = URLDecoder.decode(request.params(":name"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - server Name: {} Exception: {}",
                        request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                ApolloApplication a = getAppByName(appName);
                if (a != null) {
                    Logger.debug("Found application {}", appName);
                    return mapper.getDTOFromApp(a);
                } else {
                    Logger.info("Could not find application {}", appName);
                    return "Application " + appName + " not found!";
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", "localhost", ex);
                response.status(500);
                return "Application could not be found due to a severe database error";
            }
        }, new JsonTransformer());

        post("/applications", (request, response) -> {
            String body = request.body();
            return "Hello World: " + request.body();
        });

        //return all networks related to application with ID = ':id'
        get("/applications/:name/networks", "application/json", (Request request, Response response) -> {
            String appName;
            //decode the URL as it may contain escape characters, etc.
            try {
                appName = URLDecoder.decode(request.params(":name"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - server Name: {} Exception: {}", request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                List<ApolloNetwork> lNet = getAppNetworks(appName);
                if (lNet != null) {
                    Logger.debug("Found networks for application {}", appName);
                    List<ApolloNetworkDTO> dtoNets = new ArrayList();
                    for (ApolloNetwork aNet : lNet) {
                        dtoNets.add(mapper.getDTOFromNetwork(aNet));
                    }
                    return dtoNets;
                } else {
                    response.status(404);
                    Logger.info("Could not find any networks for application {}", appName);
                    return "Could not find any networks for application: " + appName;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", "localhost", ex);
                response.status(500);
                return "Severe error: unknown database host";
            }
        }, new JsonTransformer());

        //return all servers related to application with ID = ':id'
        get("/applications/:id/servers", "application/json", (Request request, Response response) -> {
            String appName = "";
            //decode the URL as it may contain escape characters, etc.
            try {
                appName = URLDecoder.decode(request.params(":name"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - server Name: {} Exception: {}",
                        request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                List<ApolloServer> lSrv = getAppServers(appName);
                if (lSrv != null) {
                    Logger.debug("Found servers for application {}", appName);
                    List<ApolloServerDTO> dtoSrvs = new ArrayList();
                    for (ApolloServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                } else {
                    Logger.info("Could not find servers for application {}", appName);
                    response.status(404);
                    return "Could not find servers for application: " + appName;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", "localhost", ex);
                return "Severe error: unknown database host";
            }
        }, new JsonTransformer());

        //return all servers related to application with ID = ':appID' AND network with ID = ':nID'
        get("/applications/:appname/networks/:netstart/:netend/servers", "application/json", (request, response) -> {
            String appName = "", netStart = "", netEnd = "";
            //decode the URL as it may contain escape characters, etc.
            try {
                appName = URLDecoder.decode(request.params(":appname"), "UTF-8");
                netStart = URLDecoder.decode(request.params(":netstart"), "UTF-8");
                netEnd = URLDecoder.decode(request.params(":netend"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - application {} with network start: {} and end: {} Exception {}",
                        request.params(":appname"), request.params(":netstart"), request.params(":netend"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }

            //get the servers for app network
            try {
                List<ApolloServer> lSrv = getAppNetworkServers(appName, netStart, netEnd);
                if (lSrv == null || lSrv.isEmpty()) {
                    Logger.info("No servers for application {} with network start: {} and end: {}",
                            appName, netStart, netEnd);
                    response.status(404);
                    return "No servers for application " + appName + " with network start: " + netStart + " and end: " + netEnd;
                } else {
                    Logger.debug("Found servers for application {} with network start: {} and end: ",
                            appName, netStart, netEnd);
                    List<ApolloServerDTO> dtoSrvs = new ArrayList();
                    for (ApolloServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - {} {} {} Exception {}", appName, netStart, netEnd, ex);
                return "Severe Error: Unsupported encoding in URL";
            }
        }, new JsonTransformer());

        get("/networks", "application/json", (request, response) -> {
            try {
                List<ApolloNetwork> nets = getNetworksFromDB();
                if (nets == null) {
                    Logger.info("No networks discovered");
                    return "No networks discovered";
                } else {
                    response.status(200);
                    Logger.debug("Found networks");
                    List<ApolloNetworkDTO> dtoNets = new ArrayList();
                    for (ApolloNetwork n : nets) {
                        dtoNets.add(mapper.getDTOFromNetwork(n));
                    }
                    return dtoNets;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", "localhost");
                return "Severe Error: Unknown host";
            }
        }, new JsonTransformer());

        get("/networks/:netstart/:netend", "application/json", (request, response) -> {
            String netStart = "", netEnd = "";
            //decode the URL as it may contain escape characters, etc.
            try {
                netStart = URLDecoder.decode(request.params(":netstart"), "UTF-8");
                netEnd = URLDecoder.decode(request.params(":netend"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - netStart: {} netEnd: {} Exception: {}",
                        request.params(":netstart"), request.params(":netend"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                ApolloNetwork n = getNetworkFromDB(netStart, netEnd);
                if (n == null) {
                    Logger.info("No network with start {} and end {} found", netStart, netEnd);
                    return "No network with start " + netStart + " and end " + netEnd + " found";
                } else {
                    Logger.debug("Found network with start {} and end {} ", netStart, netEnd);
                    return mapper.getDTOFromNetwork(n);
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", "localhost", ex);
                return "Severe Error: Unknown host";
            }
        }, new JsonTransformer());

        get("/networks/:netstart/:netend/servers", "application/json", (request, response) -> {
            String netStart = "", netEnd = "";
            //decode the URL as it may contain escape characters, etc.
            try {
                netStart = URLDecoder.decode(request.params(":netstart"), "UTF-8");
                netEnd = URLDecoder.decode(request.params(":netend"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - netStart: {} netEnd: {} Exception: {}",
                        request.params(":netstart"), request.params(":netend"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                List<ApolloServer> lSrv = getNetworkServersFromDB(netStart, netEnd);
                if (lSrv == null) {
                    Logger.info("No servers in network with start {} and end {} found", netStart, netEnd);
                    return "No servers in network with start " + netStart + " and end " + netEnd + " found";
                } else {
                    Logger.debug("Found servers in network with start {} and end {} ", netStart, netEnd);
                    List<ApolloServerDTO> dtoSrvs = new ArrayList();
                    for (ApolloServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", "localhost", ex);
                return "Severe Error: Unknown host";
            }
        }, new JsonTransformer());

        get("/servers", "application/json", (request, response) -> {
            try {
                List<ApolloServer> srvs = getServersFromDB();
                if (srvs == null) {
                    Logger.info("Found no servers in database");
                    return "No Networks";
                } else {
                    Logger.debug("Found servers in database");
                    List<ApolloServerDTO> dtoSrvs = new ArrayList();
                    for (ApolloServer s : srvs) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    response.status(200);
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", "localhost");
                return "Severe Error: Unknown host";
            }
        }, new JsonTransformer());

        get("/servers/:name", "application/json", (request, response) -> {
            String srvName;
            try {
                srvName = URLDecoder.decode(request.params(":name"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - {} Exception {}", request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                ApolloServer s = getServerFromDB(srvName);
                if (s == null) {
                    Logger.info("No server with name {} found", srvName);
                    return "No server with name " + srvName;

                } else {
                    Logger.debug("Found server with name {}", srvName);
                    return mapper.getDTOFromServer(s);
                }

            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", "localhost");
                return "Severe Error: Unknown host";
            }
        }, new JsonTransformer());

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
        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloNetworkRepositoryMongoDBImpl netDB = new ApolloNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloServerRepositoryMongoDBImpl srvDB = new ApolloServerRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");

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
    }

    private static List<ApolloApplication> getApplicationsFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloApplication> l = appDB.list();

        //close the db client
        mongoClient.close();

        return l;
    }

    private static List<ApolloNetwork> getNetworksFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloNetworkRepositoryMongoDBImpl appDB = new ApolloNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloNetwork> l = appDB.list();

        //close the db client
        mongoClient.close();
        return l;
    }

    private static List<ApolloServer> getServersFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloServerRepositoryMongoDBImpl appDB = new ApolloServerRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloServer> l = appDB.list();

        //close the db client
        mongoClient.close();
        return l;
    }

    private static ApolloApplication getAppByName(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloApplication a = appDB.getAppByName(appName);

        //close the db client
        mongoClient.close();

        return a;
    }

    private static List<ApolloNetwork> getAppNetworks(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloNetwork> lNet = appDB.getAppNetworks(appName);

        //close the db client
        mongoClient.close();

        return lNet;
    }

    private static List<ApolloServer> getAppServers(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloServer> lSrv = appDB.getAppServers(appName);

        //close the db client
        mongoClient.close();

        return lSrv;
    }

    private static List<ApolloServer> getAppNetworkServers(String appName, String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloApplicationRepositoryMongoDBImpl appDB = new ApolloApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        List<ApolloServer> lSrv = appDB.getNetworkServers(appName, netStart, netEnd);

        //close the db client
        mongoClient.close();

        return lSrv;
    }

    private static ApolloNetwork getNetworkFromDB(String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloNetworkRepositoryMongoDBImpl netDB = new ApolloNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloNetwork aNet = netDB.getNetByStartAndEnd(netStart, netEnd);

        //close the db client
        mongoClient.close();

        return aNet;
    }

    private static List<ApolloServer> getNetworkServersFromDB(String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloNetworkRepositoryMongoDBImpl netDB = new ApolloNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloNetwork aNet = netDB.getNetByStartAndEnd(netStart, netEnd);

        //close the db client
        mongoClient.close();

        return aNet.getServers();
    }

    private static ApolloServer getServerFromDB(String srvName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient("localhost");

        ApolloServerRepositoryMongoDBImpl srvDB = new ApolloServerRepositoryMongoDBImpl(mongoClient, morphia, "Apollo");
        ApolloServer aSrv = srvDB.getServerByName(srvName);

        //close the db client
        mongoClient.close();

        return aSrv;
    }

}
