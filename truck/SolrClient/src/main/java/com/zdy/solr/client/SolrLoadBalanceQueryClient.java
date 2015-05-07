/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.solr.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author zdy
 */
public class SolrLoadBalanceQueryClient {

    public static void main(String[] args) {

        try {
            //String url = "http://localhost:7070/solr/collection1";

            LBHttpSolrServer server = new LBHttpSolrServer("http://host1:8080/solr/", "http://host2:8080/solr", "http://host3:8080/solr");

            //or if you wish to pass the HttpClient do as follows
            //httpClient httpClient =  new HttpClient();
            //SolrServer lbHttpSolrServer = new LBHttpSolrServer(httpClient,"http://host1:8080/solr/","http://host2:8080/solr","http://host3:8080/solr");

            //remove one 
            server.removeSolrServer("http://host4:8080/solr");
            //and add another
            server.addSolrServer("http://host5:8080/solr");

            server.setAliveCheckInterval(60 * 1000); //time in milliseconds
            server.setConnectionTimeout(5000); // 5 seconds to establish TCP
            // Setting the XML response parser is only required for cross
            // version compatibility and only when one side is 1.4.1 or
            // earlier and the other side is 3.1 or later.
            server.setParser(new XMLResponseParser()); // binary parser is used by default
            // The following settings are provided here for completeness.
            // They will not normally be required, and should only be used 
            // after consulting javadocs to know whether they are truly required.
            server.setSoTimeout(1000);  // socket read timeout

            SolrQuery query = new SolrQuery();
            query.setQuery("*:*");
            query.setSort("price", SolrQuery.ORDER.asc);

            query.setFacet(true).setFacetMinCount(1).setFacetLimit(8);

            QueryResponse rsp = server.query(query);

            SolrDocumentList docs = rsp.getResults();


            server.shutdown();

            for (SolrDocument doc : docs) {
                System.out.println(doc.toString());
            }

        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrLoadBalanceQueryClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
