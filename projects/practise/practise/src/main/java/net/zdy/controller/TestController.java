/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zdy.controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.zdy.conf.MongoConfig;
import net.zdy.pojo.Greeting;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author breeze
 */
@Controller
@RequestMapping("test")
public class TestController {

    @Autowired
    private MongoConfig mongoConfig;

    //@Autowired
    //private TestService testService;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /*
     RequestMapping注解有六个属性，下面我们把她分成三类进行说明。
     1、 value， method；
     value：     指定请求的实际地址，指定的地址可以是URI Template 模式（后面将会说明）；
     method：  指定请求的method类型， GET、POST、PUT、DELETE等；

     2、 consumes，produces；
     consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
     produces:    指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；

     3、 params，headers；
     params： 指定request中必须包含某些参数值是，才让该方法处理。
     headers： 指定request中必须包含某些指定的header值，才能让该方法处理请求。
     */
    @RequestMapping(value = "greeting", method = RequestMethod.GET)
    @ResponseBody
    public Greeting HandGreetingRequest(@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
    
    @RequestMapping(value = "greeting/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Greeting HandGreeting2Request(@PathVariable("name") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value ="env", method = RequestMethod.GET)
    @ResponseBody
    public Object handENVRequest() throws Exception {
        return System.getenv();
    }

    @RequestMapping(value ="hello", method = RequestMethod.GET)
    @ResponseBody
    public Set<?> handHelloRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConfig.getUsername(), mongoConfig.getDatabase(), mongoConfig.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConfig.getHost(), mongoConfig.getPort()), Arrays.asList(credential));
        DB db = mongoClient.getDB(mongoConfig.getDatabase());

        Set<String> collNameSet = db.getCollectionNames();

        mongoClient.close();
        return collNameSet;
    }

    @RequestMapping(value ="picture", method = RequestMethod.GET)
    public void handPictureRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("image/jpeg");

        //MongoCredential credential = MongoCredential.createMongoCRCredential(mongoConfig.getUsername(), mongoConfig.getDatabase(), mongoConfig.getPassword().toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(mongoConfig.getHost(), mongoConfig.getPort()));//, Arrays.asList(credential));
        DB db = mongoClient.getDB(mongoConfig.getDatabase());

        byte[] picture = null;// testService.imageService(db);

        mongoClient.close();
        response.getOutputStream().write(picture);
    }

    @RequestMapping(value ="uploadFile")
    public void handUploadRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        //验证是否使用 FileUpload 组件  
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            response.getWriter().println("没有文件域");
            return;
        }

        String fileName = null;  //上传的文件名称
        InputStream inStream = null; //上传的文件数据的输入流
        Map<String, String> paramMap = new HashMap<>();

        //获取数据
        FileItemFactory itemFactory = new DiskFileItemFactory();
        ServletFileUpload fileUpload = new ServletFileUpload(itemFactory);

        //Map<String, List<FileItem>> localFileMap = upload.parseParameterMap(request);
        List<FileItem> localFileList = fileUpload.parseRequest(request);

        for (FileItem fileItem : localFileList) {
            //验证是否为表单域      
            if (fileItem.isFormField()) {
                //是表单域, 把获取到得 表单域的 fieldName 和 value 放在 map 中.  

                //获得表单中该字段的字段名     
                String fieldName = fileItem.getFieldName();

                //获得表单中与上面字段名对应的字段值        
                String value = fileItem.getString("UTF-8");

                paramMap.put(fieldName, value);
            } else {
              //不是表单域, 即为文件域. 获取 FileItem 对象, 注意限制条件

                //这里获得了与file对应的FileItem对象     
                String fieldName = fileItem.getFieldName();
                if ("myfile".equals(fieldName)) {
                    fileName = fileItem.getName();
                    long localFileLen = fileItem.getSize();
                    if (localFileLen > 0) {
                        inStream = fileItem.getInputStream();
                    }
                }
            }
        }

        String fileType = null;

        if (null != inStream) {
            inStream.close();
        }

    }

}
