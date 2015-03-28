
import com.zdy.solr.service.SolrSimpleQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author zdy
 */
@Configuration
@ComponentScan
public class AppTest {

    @Autowired
    SolrSimpleQueryService solrSimpleQueryService;

    public static void main(String[] args) {

        ApplicationContext context= new AnnotationConfigApplicationContext(AppTest.class);

        //SolrSimpleQueryService solrSimpleQueryService=context.getBean(SolrSimpleQueryService.class);
        //solrSimpleQueryService.service();
        context.getBean(AppTest.class).test();
        
    }

    public void test() {
        solrSimpleQueryService.service();
    }

}
