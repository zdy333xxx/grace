/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zdy
 */
public class AppTest {

    public AppTest() {
        
        System.out.println("init AppTest......");
    }

    @BeforeClass
    public static void setUpClass() {
        
        System.out.println("BeforeClass......");
    }

    @AfterClass
    public static void tearDownClass() {
        
        System.out.println("AfterClass......");
    }

    @Before
    public void setUp() {

        System.out.println("before......");
    }

    @After
    public void tearDown() {

        System.out.println("after......");
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {

        System.out.println("test......");
    }
}
