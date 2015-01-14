/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author schari
 */
public class GeminiEnvironmentDTO extends GeminiBaseDTO {

    private String type;
    private String name;

    @Embedded
    private List<GeminiApplicationDTO> applications = Collections.synchronizedList(new ArrayList());

    @Embedded
    private List<GeminiNetworkDTO> networks;

    @Embedded
    private List<GeminiServerDTO> servers;

}
