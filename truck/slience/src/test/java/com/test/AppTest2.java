/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zdy
 */
public class AppTest2 {

    public static void main(String[] args) {

        try {
            String line = new BufferedReader(new FileReader(AppTest2.class.getResource("/words.txt").getPath())).readLine();

            System.out.println(line);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppTest2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppTest2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
