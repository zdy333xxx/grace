/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.solr.cloud.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author zdy
 */
public class SolrCloudInsertClient {

    public static void main(String[] args) {

        try {
            String zooKeeperHost = "localhost:9983";  // ZooKeeper host or hosts

            CloudSolrServer server = new CloudSolrServer(zooKeeperHost);

            server.setDefaultCollection("collection1");

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1234");
            doc.addField("name", "A lovely summer holiday");

            UpdateResponse updateResponse = server.add(doc);

            server.commit();

            server.shutdown();

            System.out.println(updateResponse.toString());

        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrCloudInsertClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
