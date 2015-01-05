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
public class ApolloNetworkDTO extends BaseDTO  {

    private String start;
    private String end;
    private String networkType;
    private List<ApolloServerDTO> servers;

    public ApolloNetworkDTO () {
        this.networkType = "";
        servers = new ArrayList();
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public List<ApolloServerDTO> getServers() {
        return servers;
    }

    /**
     * @param servers the servers to set
     */
    public void setServers(List<ApolloServerDTO> servers) {
        this.servers = servers;
    }
}
