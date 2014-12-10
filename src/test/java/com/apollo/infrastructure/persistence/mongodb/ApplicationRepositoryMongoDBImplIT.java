/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apollo.infrastructure.persistence.mongodb;

import com.apollo.domain.model.application.ApolloApplication;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author schari
 */
public class ApplicationRepositoryMongoDBImplIT {
    
    public ApplicationRepositoryMongoDBImplIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class ApplicationRepositoryMongoDBImpl.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        ApolloApplication newInstance = null;
        ApplicationRepositoryMongoDBImpl instance = new ApplicationRepositoryMongoDBImpl();
        instance.add(newInstance);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class ApplicationRepositoryMongoDBImpl.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Integer id = null;
        ApplicationRepositoryMongoDBImpl instance = new ApplicationRepositoryMongoDBImpl();
        ApolloApplication expResult = null;
        ApolloApplication result = instance.get(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class ApplicationRepositoryMongoDBImpl.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        ApolloApplication transientObject = null;
        ApplicationRepositoryMongoDBImpl instance = new ApplicationRepositoryMongoDBImpl();
        instance.update(transientObject);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class ApplicationRepositoryMongoDBImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        Integer id = null;
        ApplicationRepositoryMongoDBImpl instance = new ApplicationRepositoryMongoDBImpl();
        instance.delete(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class ApplicationRepositoryMongoDBImpl.
     */
    @Test
    public void testList() {
        System.out.println("list");
        ApplicationRepositoryMongoDBImpl instance = new ApplicationRepositoryMongoDBImpl();
        List<ApolloApplication> expResult = null;
        List<ApolloApplication> result = instance.list();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
