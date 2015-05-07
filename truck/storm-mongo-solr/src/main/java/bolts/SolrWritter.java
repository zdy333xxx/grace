/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bolts;

import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrWritter implements IRichBolt {

    Map conf;
    Integer id;
    String name;
    Map<String, Integer> counters;
    private OutputCollector collector;
    HttpSolrServer server;
    int num;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 这个spout结束时（集群关闭的时候），我们会显示单词数量
     */
    @Override
    public void cleanup() {
        if (null != server) {
            server.shutdown();
        }

        System.out.println("-- 提交结束 【" + name + "-" + id + "】 --" + num + " 条记录");

    }

    /**
     * 为每个单词计数
     *
     * @param input
     */
    @Override
    public void execute(Tuple input) {
        //DBObject doc = (DBObject) input.getValue(0);
        System.out.println("------------------------------------------");

        /**
         * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
         */
        List<Map<String, Object>> docMapList = (List<Map<String, Object>>) input.getValue(0);

        Collection<SolrInputDocument> docs = new ArrayList<>();

        for (Map<String, Object> docMap : docMapList) {
            SolrInputDocument doc = new SolrInputDocument();

            for (Entry<String, Object> entry : docMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if ("id".equals(key)) {
                    doc.addField(key, String.valueOf(value));
                } else if (value instanceof Long) {
                    doc.addField(key + "_l", (Long) value, 1.0f);
                } else if (value instanceof Double) {
                    doc.addField(key + "_d", (Double) value, 1.0f);
                } else if (value instanceof Float) {
                    doc.addField(key + "_f", (Float) value);
                } else if (value instanceof Integer) {
                    doc.addField(key + "_i", (Integer) value);
                } else if (value instanceof String) {
                    doc.addField(key + "_s", (String) value);
                    doc.addField(key + "_txt", (String) value);
                } else if (value instanceof Date) {
                    doc.addField(key + "_s", sdf.format((Date) value));
                    doc.addField(key + "_txt", sdf.format((Date) value));
                } else {
                    doc.addField(key + "_s", value.toString());
                    doc.addField(key + "_txt", value.toString());
                }
            }

            docs.add(doc);
        }

        try {
            UpdateResponse updateResponse = server.add(docs);
            System.out.println(updateResponse.toString());

            if (updateResponse.getStatus() == 0) {
                server.commit();

                num += docs.size();
                System.out.println("-- 提交 【" + name + "-" + id + "】 --" + num + " 条记录");

                //对元组做出应答
                collector.ack(input);
            }

        } catch (SolrServerException | IOException ex) {
            Logger.getLogger(SolrWritter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 初始化
     *
     * @param conf
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        //this.counters = new HashMap<String, Integer>();
        this.collector = collector;
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();

        String solr_url = (String) conf.get("solr_url");
        server = new HttpSolrServer(solr_url);

        server.setMaxRetries(0); // defaults to 0.  > 1 not recommended.
        server.setConnectionTimeout(5000); // 5 seconds to establish TCP
        // Setting the XML response parser is only required for cross
        // version compatibility and only when one side is 1.4.1 or
        // earlier and the other side is 3.1 or later.
        server.setParser(new XMLResponseParser()); // binary parser is used by default
        // The following settings are provided here for completeness.
        // They will not normally be required, and should only be used 
        // after consulting javadocs to know whether they are truly required.
        server.setSoTimeout(10000);  // socket read timeout
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxTotalConnections(100);
        server.setFollowRedirects(false);  // defaults to false
        // allowCompression defaults to false.
        // Server side must support gzip or deflate for this to have any effect.
        server.setAllowCompression(true);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
