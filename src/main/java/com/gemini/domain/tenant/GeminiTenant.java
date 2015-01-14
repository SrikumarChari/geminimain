/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.tenant;

import com.gemini.common.repository.EntityMongoDB;
import com.gemini.domain.model.GeminiEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.pmw.tinylog.Logger;

/**
 *
 * @author schari
 */
@Entity
public class GeminiTenant extends EntityMongoDB {
    private String name;
    private String tenantID;
    
    @Embedded
    private List<GeminiTenantUser> users = Collections.synchronizedList(new ArrayList());
    
    @Embedded
    private List<GeminiEnvironment> environments = Collections.synchronizedList(new ArrayList());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public List<GeminiTenantUser> getUsers() {
        return users;
    }

    public void setUsers(List<GeminiTenantUser> users) {
        this.users = users;
    }

    public boolean addUser (GeminiTenantUser user) {
        if (users.contains(user)) {
            Logger.debug("Did not add user, {} already exists in tenant {}", user.getName(), getName());
            return false;
        } else {
            if (!users.add(user)) {
                Logger.error("Failed to add user {} to tenant {}, system error", user.getName(), getName());
                return false;
            } else {
                Logger.debug("Successfully added user {} to tenant {}", user.getName(), getName());
                return true;
            }
        }
    }
    
    public boolean deleteUser(GeminiTenantUser user) {
        if (users.contains(user)) {
            if (!users.remove(user)) {
                Logger.error("Failed to remove user {} from tenant {} - system error", user.getName(), getName());
                return false;
            } else {
                Logger.debug("Successfully deleted user {} from tenant {}", user.getName(), getName());
                return true;
            }
        } else {
            Logger.debug("Did not delete user {} from tenant {} - does not exist", user.getName(), getName());
            return false;
        }
    }
    
    public List<GeminiEnvironment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<GeminiEnvironment> environments) {
        this.environments = environments;
    }
    
    public boolean addEnvironment (GeminiEnvironment env) {
        if (environments.contains(env)) {
            Logger.debug("Did not add environment, {} already exists in tenant {}", env.getName(), getName());
            return false;
        } else {
            if (!environments.add(env)) {
                Logger.error("Failed to add environment {} to tenant {}, system error", env.getName(), getName());
                return false;
            } else {
                Logger.debug("Successfully added environment {} to tenant {}", env.getName(), getName());
                return true;
            }
        }
    }
    
    public boolean deleteEnvironment(GeminiEnvironment env) {
        if (environments.contains(env)) {
            if (!environments.remove(env)) {
                Logger.error("Failed to remove environment {} from tenant {} - system error", env.getName(), getName());
                return false;
            } else {
                Logger.debug("Successfully deleted environment {} from tenant {}", env.getName(), getName());
                return true;
            }
        } else {
            Logger.debug("Did not delete environment {} from tenant {} - does not exist", env.getName(), getName());
            return false;
        }
    }
}
