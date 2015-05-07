/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.solr.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author zdy
 */
public class SolrInsertClient {

    public static void main(String[] args) {

        try {
            String url = "http://localhost:7070/solr/collection1";
            /*
             HttpSolrServer is thread-safe and if you are using the following constructor,
             you *MUST* re-use the same instance for all requests.  If instances are created on
             the fly, it can cause a connection leak. The recommended practice is to keep a
             static instance of HttpSolrServer per solr server url and share it for all requests.
             See https://issues.apache.org/jira/browse/SOLR-861 for more details
             */
            HttpSolrServer server = new HttpSolrServer(url);

            server.setMaxRetries(0); // defaults to 0.  > 1 not recommended.
            server.setConnectionTimeout(5000); // 5 seconds to establish TCP
            // Setting the XML response parser is only required for cross
            // version compatibility and only when one side is 1.4.1 or
            // earlier and the other side is 3.1 or later.
            server.setParser(new XMLResponseParser()); // binary parser is used by default
            // The following settings are provided here for completeness.
            // They will not normally be required, and should only be used 
            // after consulting javadocs to know whether they are truly required.
            server.setSoTimeout(1000);  // socket read timeout
            server.setDefaultMaxConnectionsPerHost(100);
            server.setMaxTotalConnections(100);
            server.setFollowRedirects(false);  // defaults to false
            // allowCompression defaults to false.
            // Server side must support gzip or deflate for this to have any effect.
            server.setAllowCompression(true);

            //server.deleteByQuery( "*:*" );// CAUTION: deletes everything!
            SolrInputDocument doc1 = new SolrInputDocument();
            doc1.addField("id", "id1", 1.0f);
            doc1.addField("name", "doc1", 1.0f);
            doc1.addField("price", 10);

            SolrInputDocument doc2 = new SolrInputDocument();
            doc2.addField("id", "id2", 1.0f);
            doc2.addField("name", "doc2", 1.0f);
            doc2.addField("price", 20);

            Collection<SolrInputDocument> docs = new ArrayList<>();
            docs.add(doc1);
            docs.add(doc2);

            UpdateResponse updateResponse = server.add(docs);

            server.commit();

            /*
             UpdateRequest req = new UpdateRequest();
             req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
             req.add( docs );
             UpdateResponse rsp = req.process( server );
             */
            server.shutdown();

            System.out.println(updateResponse.toString());

        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrInsertClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
