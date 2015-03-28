
import com.zdy.picture.module.service.MongoPictureService;
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
public class Application {

    @Autowired
    private MongoPictureService mongoPictureService;

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        //SolrSimpleQueryService solrSimpleQueryService=context.getBean(SolrSimpleQueryService.class);
        //solrSimpleQueryService.service();

        context.getBean(Application.class).test();
    }

    public void test() {
        mongoPictureService.service();
    }

}
