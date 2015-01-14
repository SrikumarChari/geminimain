/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.geminimain;

import com.gemini.domain.dto.GeminiApplicationDTO;
import com.gemini.domain.dto.GeminiEnvironmentDTO;
import com.gemini.domain.dto.GeminiNetworkDTO;
import com.gemini.domain.dto.GeminiServerDTO;
import com.gemini.domain.model.GeminiServer;
import com.gemini.domain.model.GeminiNetwork;
import com.gemini.domain.model.GeminiApplication;
import com.gemini.domain.model.GeminiEnvironment;
import com.gemini.domain.repository.impl.GeminiApplicationRepositoryMongoDBImpl;
import com.gemini.domain.repository.impl.GeminiNetworkRepositoryMongoDBImpl;
import com.gemini.domain.repository.impl.GeminiServerRepositoryMongoDBImpl;
import com.gemini.domain.tenant.GeminiTenant;
import com.gemini.mapper.GeminiMapper;
import com.gemini.mapper.GeminiMapperModule;
import com.gemini.tenant.repository.impl.GeminiTenantRepositoryMongoDBImpl;
import com.google.common.net.InetAddresses;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.util.ArrayList;
import static spark.Spark.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import static org.apache.http.HttpHeaders.USER_AGENT;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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
public class GeminiMain {

    static ArrayList<GeminiApplication> applications = new ArrayList<>();
    static ArrayList<GeminiServer> servers = new ArrayList<>();
    static ArrayList<GeminiNetwork> networks = new ArrayList<>();
    static MongoClient mongoClient;
    static Morphia morphia = new Morphia();
    static Datastore ds;
    static boolean autoDiscover = true;
    static String discoveryRestUrl = "localhost:1234/";
    static String DB_SERVER = "localhost";

    public static void main(String[] args) throws IOException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        //create table for the application, networks and servers
        ds = morphia.createDatastore(mongoClient, "Gemini");

        //create the mapper
        Injector injector = Guice.createInjector(new GeminiMapperModule());
        GeminiMapper mapper = injector.getInstance(GeminiMapper.class);

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

        //get all environments of the tenant
        get("/environments/:tenantid", "application/json", (request, response) -> {
            String tenantID = request.params("tenantid");
            try {
                List<GeminiEnvironment> lEnvs = getEnvironments(tenantID);
                if (lEnvs != null) {
                    response.status(200);
                    Logger.info("Found environments for tenant {}", tenantID);
                    List<GeminiEnvironmentDTO> dtoEnvs = new ArrayList();
                    lEnvs.stream().forEach(e -> dtoEnvs.add(mapper.getDTOFromEnv(e)));
                    return lEnvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", DB_SERVER);
                response.status(500);
                return "Severe Error: Unknown database host " + DB_SERVER;
            }

            return "not implemented yet";
        }, new JsonTransformer());

        //return all applications for a given tenant and environment
        get("/applications/:tenantid/:envname", "application/json", (request, response) -> {
            String tenantID, envName;
            try {
                tenantID = URLDecoder.decode(request.params(":tenantid"), "UTF-8");
                envName = URLDecoder.decode(request.params(":envname"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - server Name: {} Exception: {}", request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }

            try {
                List<GeminiApplication> apps = getEnvApplications(tenantID, envName);
                if (apps == null || apps.isEmpty()) {
                    response.status(404);
                    Logger.info("Could not find any applications.");
                    return "No Applications found.";
                } else {
                    response.status(200);
                    Logger.debug("Found applications");
                    List<GeminiApplicationDTO> dtoApps = new ArrayList();
                    apps.stream().forEach((a) -> {
                        dtoApps.add(mapper.getDTOFromApp(a));
                    });
                    return dtoApps;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {}", DB_SERVER);
                response.status(500);
                return "Severe Error: Unknown database host " + DB_SERVER;
            }
        }, new JsonTransformer());

        //return application given a name
        get("/applications/:name", "application/json", (request, response) -> {
            String appName = "";
            //decode the URL as it may contain escape characters, etc.
            try {
                appName = URLDecoder.decode(request.params(":name"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - application Name: {} Exception: {}",
                        request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL - server name " + appName;
            }
            try {
                GeminiApplication a = getAppByName(appName);
                if (a != null) {
                    Logger.debug("Found application {}", appName);
                    return mapper.getDTOFromApp(a);
                } else {
                    Logger.info("Could not find application {}", appName);
                    return "Could not find application " + appName;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                response.status(500);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
                List<GeminiNetwork> lNet = getAppNetworks(appName);
                if (lNet != null) {
                    Logger.debug("Found networks for application {}", appName);
                    List<GeminiNetworkDTO> dtoNets = new ArrayList();
                    lNet.stream().forEach(aNet -> dtoNets.add(mapper.getDTOFromNetwork(aNet)));
                    return dtoNets;
                } else {
                    response.status(404);
                    Logger.info("Could not find any networks for application {}", appName);
                    return "Could not find any networks for application: " + appName;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                response.status(500);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
                List<GeminiServer> lSrv = getAppServers(appName);
                if (lSrv != null) {
                    Logger.debug("Found servers for application {}", appName);
                    List<GeminiServerDTO> dtoSrvs = new ArrayList();
                    for (GeminiServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                } else {
                    Logger.info("Could not find servers for application {}", appName);
                    response.status(404);
                    return "Could not find servers for application: " + appName;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
                List<GeminiServer> lSrv = getAppNetworkServers(appName, netStart, netEnd);
                if (lSrv == null || lSrv.isEmpty()) {
                    Logger.info("No servers for application {} with network start: {} and end: {}",
                            appName, netStart, netEnd);
                    response.status(404);
                    return "No servers for application " + appName + " with network start: " + netStart + " and end: " + netEnd;
                } else {
                    Logger.debug("Found servers for application {} with network start: {} and end: ",
                            appName, netStart, netEnd);
                    List<GeminiServerDTO> dtoSrvs = new ArrayList();
                    for (GeminiServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
            }
        }, new JsonTransformer());

        //get the networks for a tenant and environment,
        get("/networks/:tenantid/:envname", "application/json", (request, response) -> {
            String tenantID, envName;
            try {
                tenantID = URLDecoder.decode(request.params(":tenantid"), "UTF-8");
                envName = URLDecoder.decode(request.params(":envname"), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.error("Severe Error: Unsupported encoding in URL - server Name: {} Exception: {}", request.params(":name"), ex);
                return "Severe Error: Unsupported encoding in URL";
            }
            try {
                List<GeminiNetwork> nets = getEnvNetworks(tenantID, envName);
                if (nets == null) {
                    Logger.info("No networks discovered for tenant {} in environment {}", tenantID, envName);
                    return "No networks discovered for tenant" + tenantID + "in environment" + envName;
                } else {
                    response.status(200);
                    List<GeminiNetworkDTO> dtoNets = new ArrayList();
                    nets.stream().forEach(n -> dtoNets.add(mapper.getDTOFromNetwork(n)));
                    Logger.debug("Found {} networks for tenant {} env {}", nets.size(), tenantID, envName);
                    return dtoNets;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown datbase host - {}", DB_SERVER);
                return "Severe Error: Unknown data host " + DB_SERVER;
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
                GeminiNetwork n = getNetworkFromDB(netStart, netEnd);
                if (n == null) {
                    Logger.info("No network with start {} and end {} found", netStart, netEnd);
                    return "No network with start " + netStart + " and end " + netEnd + " found";
                } else {
                    Logger.debug("Found network with start {} and end {} ", netStart, netEnd);
                    return mapper.getDTOFromNetwork(n);
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
                List<GeminiServer> lSrv = getNetworkServersFromDB(netStart, netEnd);
                if (lSrv == null) {
                    Logger.info("No servers in network with start {} and end {} found", netStart, netEnd);
                    return "No servers in network with start " + netStart + " and end " + netEnd + " found";
                } else {
                    Logger.debug("Found servers in network with start {} and end {} ", netStart, netEnd);
                    List<GeminiServerDTO> dtoSrvs = new ArrayList();
                    for (GeminiServer s : lSrv) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
            }
        }, new JsonTransformer());

        post("/networks/:netstart/:netend", "application/json", (request, response) -> {
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

            //return the discovered networks
//            DiscoverNetworkRange newNet = new DiscoverNetworkRange(netStart, netEnd);
//            List<GeminiNetwork> lNet;
//            if (autoDiscover) {
//                try {
//                    //start discovering...
//                    lNet = discoverNetworks(netStart, netEnd);
//                } catch (IOException ex) {
//                    java.util.logging.Logger.getLogger(GeminiMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//                }
//            }

            //since all the services are running on the same computer
            response.header("Access-Control-Allow-Origin", "*");

            //return the networks...
            return "no networks";
        }, new JsonTransformer());

        //get all servers for tenant within an environment
        get("/servers", "application/json", (request, response) -> {
            try {
                List<GeminiServer> srvs = getServersFromDB();
                if (srvs == null) {
                    Logger.info("Found no servers in database");
                    return "No Networks";
                } else {
                    Logger.debug("Found servers in database");
                    List<GeminiServerDTO> dtoSrvs = new ArrayList();
                    for (GeminiServer s : srvs) {
                        dtoSrvs.add(mapper.getDTOFromServer(s));
                    }
                    response.status(200);
                    return dtoSrvs;
                }
            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
                GeminiServer s = getServerFromDB(srvName);
                if (s == null) {
                    Logger.info("No server with name {} found", srvName);
                    return "No server with name " + srvName;

                } else {
                    Logger.debug("Found server with name {}", srvName);
                    return mapper.getDTOFromServer(s);
                }

            } catch (UnknownHostException ex) {
                Logger.error("Severe Error: Unknown host - {} Exception: {}", DB_SERVER, ex);
                return "Severe Error: Unknown database host " + DB_SERVER;
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
        ds.getCollection(GeminiApplication.class).drop();
        ds.getCollection(GeminiNetwork.class).drop();
        ds.getCollection(GeminiServer.class).drop();

        //create the application repository object
        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiNetworkRepositoryMongoDBImpl netDB = new GeminiNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiServerRepositoryMongoDBImpl srvDB = new GeminiServerRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");

        //first the server objects
        Random sRand = new Random();
        for (int i = 0; i < numServers; i++) {
            String serverID = String.valueOf(sRand.nextInt((serverMax - serverMin) + 1) + serverMin);
            GeminiServer s = new GeminiServer();
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
            GeminiNetwork n = new GeminiNetwork();
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
            GeminiApplication a = new GeminiApplication();
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

    private static List<GeminiApplication> getApplicationsFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiApplication> l = appDB.list();

        //close the db client
        mongoClient.close();

        return l;
    }

    private static List<GeminiNetwork> getNetworksFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiNetworkRepositoryMongoDBImpl appDB = new GeminiNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiNetwork> l = appDB.list();

        //close the db client
        mongoClient.close();
        return l;
    }

    private static List<GeminiServer> getServersFromDB() throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiServerRepositoryMongoDBImpl appDB = new GeminiServerRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiServer> l = appDB.list();

        //close the db client
        mongoClient.close();
        return l;
    }

    private static GeminiApplication getAppByName(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiApplication a = appDB.getAppByName(appName);

        //close the db client
        mongoClient.close();

        return a;
    }

    private static List<GeminiNetwork> getAppNetworks(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiNetwork> lNet = appDB.getAppNetworks(appName);

        //close the db client
        mongoClient.close();

        return lNet;
    }

    private static List<GeminiServer> getAppServers(String appName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiServer> lSrv = appDB.getAppServers(appName);

        //close the db client
        mongoClient.close();

        return lSrv;
    }

    private static List<GeminiServer> getAppNetworkServers(String appName, String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiApplicationRepositoryMongoDBImpl appDB = new GeminiApplicationRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        List<GeminiServer> lSrv = appDB.getNetworkServers(appName, netStart, netEnd);

        //close the db client
        mongoClient.close();

        return lSrv;
    }

    private static GeminiNetwork getNetworkFromDB(String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiNetworkRepositoryMongoDBImpl netDB = new GeminiNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiNetwork aNet = netDB.getNetByStartAndEnd(netStart, netEnd);

        //close the db client
        mongoClient.close();

        return aNet;
    }

    private static List<GeminiServer> getNetworkServersFromDB(String netStart, String netEnd) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiNetworkRepositoryMongoDBImpl netDB = new GeminiNetworkRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiNetwork aNet = netDB.getNetByStartAndEnd(netStart, netEnd);

        //close the db client
        mongoClient.close();

        return aNet.getServers();
    }

    private static GeminiServer getServerFromDB(String srvName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiServerRepositoryMongoDBImpl srvDB = new GeminiServerRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiServer aSrv = srvDB.getServerByName(srvName);

        //close the db client
        mongoClient.close();

        return aSrv;
    }

    private static List<GeminiNetwork> discoverNetworks(String netStart, String netEnd) throws IOException {
        List<GeminiNetwork> lNet = null;

        final String url = "https://" + discoveryRestUrl + "/" + netStart + "/" + netEnd;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("netstart", netStart));
        urlParameters.add(new BasicNameValuePair("netend", netEnd));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        int respStatus = response.getStatusLine().getStatusCode();
        Logger.debug("Discover Networks: Http response code {}", respStatus);

        if (respStatus == 200) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            Gson gson = new Gson();
            //Type typeOfT = new TypeToken<Collection<GeminiNetworkDTO>>(){}.getType();
            lNet = gson.fromJson(result.toString(), new TypeToken<List<GeminiNetworkDTO>>() {
            }.getType());
        }

        return lNet;
    }

    private static List<GeminiEnvironment> getEnvironments(String tenantID) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiTenantRepositoryMongoDBImpl tenantDB = new GeminiTenantRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiTenant aTenant = tenantDB.getTenantByName(tenantID);
        if (aTenant == null) {
            Logger.error("Could not find tenant with ID {}", tenantID);
            return null;
        }

        //close the db client
        mongoClient.close();

        return aTenant.getEnvironments();
    }

    private static List<GeminiApplication> getEnvApplications(String tenantID, String envName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiTenantRepositoryMongoDBImpl tenantDB = new GeminiTenantRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiTenant aTenant = tenantDB.getTenantByName(tenantID);
        if (aTenant == null) {
            Logger.error("Could not find tenant with ID {}", tenantID);
            return null;
        }

        //close the db client
        mongoClient.close();

        GeminiEnvironment env = aTenant.getEnvironments().stream().filter(e -> e.getName().equals(envName)).findFirst().get();
        return env.getApplications();
    }

    private static List<GeminiNetwork> getEnvNetworks(String tenantID, String envName) throws UnknownHostException {
        //setup the mongodb access
        mongoClient = new MongoClient(DB_SERVER);

        GeminiTenantRepositoryMongoDBImpl tenantDB = new GeminiTenantRepositoryMongoDBImpl(mongoClient, morphia, "Gemini");
        GeminiTenant aTenant = tenantDB.getTenantByName(tenantID);
        if (aTenant == null) {
            Logger.error("Could not find tenant with ID {}", tenantID);
            return null;
        }

        //close the db client
        mongoClient.close();

        GeminiEnvironment env = aTenant.getEnvironments().stream().filter(e -> e.getName().equals(envName)).findFirst().get();
        return env.getNetworks();
    }
}
