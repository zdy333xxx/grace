/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cazx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author zdy
 */
@Controller
@RequestMapping("upload")
public class FileUploadController {

    //@Autowired
    //private MongoPictureService mongoPictureService;
    @RequestMapping(value = "mongo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String handPictureRequest(@PathVariable("id") String id) {

        return "pictrure " + id + " storage in mongodb document..........";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                //byte[] bytes = file.getBytes();
                //BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name)));
                //stream.write(bytes);
                //stream.close();

                System.out.println("------------------------>" + file.getName() + ":" + file.getSize());

                return "You successfully uploaded " + name + "!";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

}
