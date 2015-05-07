/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.solr.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author zdy
 */
public class SolrConcurrentUpdateClient {

    public static void main(String[] args) {

        try {
            String url = "http://localhost:7070/solr/collection1";

            ConcurrentUpdateSolrServer server = new ConcurrentUpdateSolrServer(url, 10, 1);

            server.setConnectionTimeout(5000); // 5 seconds to establish TCP
            // Setting the XML response parser is only required for cross
            // version compatibility and only when one side is 1.4.1 or
            // earlier and the other side is 3.1 or later.
            server.setParser(new XMLResponseParser()); // binary parser is used by default
            // The following settings are provided here for completeness.
            // They will not normally be required, and should only be used 
            // after consulting javadocs to know whether they are truly required.
            server.setSoTimeout(1000);  // socket read timeout
            //server.setPollQueueTime(100);

            //server.deleteByQuery( "*:*" );// CAUTION: deletes everything!
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "change.me");
            doc.addField("name", "nice change!");
            doc.addField("price", 10);

            UpdateRequest req = new UpdateRequest();
            req.setAction(UpdateRequest.ACTION.COMMIT, false, false);
            req.add(doc);
            UpdateResponse updateResponse = req.process(server);

            server.commit();

            server.shutdown();

            System.out.println(updateResponse.toString());

        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrConcurrentUpdateClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
