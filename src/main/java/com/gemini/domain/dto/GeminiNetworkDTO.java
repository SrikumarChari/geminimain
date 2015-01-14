/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiNetworkDTO extends GeminiBaseDTO  {

    private String start;
    private String end;
    private String network;
    private Integer mask;
    private String host;
    private String networkType;
    private List<GeminiServerDTO> servers;

    public GeminiNetworkDTO () {
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

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public List<GeminiServerDTO> getServers() {
        return servers;
    }

    /**
     * @param servers the servers to set
     */
    public void setServers(List<GeminiServerDTO> servers) {
        this.servers = servers;
    }
}
