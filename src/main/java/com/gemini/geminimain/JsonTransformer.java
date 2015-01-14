/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.geminimain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

/**
 *
 * @author schari
 */
public class JsonTransformer implements ResponseTransformer {
    private Gson gson;

    public JsonTransformer() {
        this.gson = new Gson();
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
