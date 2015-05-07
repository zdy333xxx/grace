<%-- 
    Document   : test
    Created on : 2014-11-18, 17:17:08
    Author     : breeze
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
        
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        <br/>
        <br/>
        <button id="test">测试</button>
        
        <div id="display"></div>
        
        <script>
            
            $("body").on("click", "#test", function() {

                var localInfoParam = {
                    
                };

                var param = JSON.stringify(localInfoParam).toString();

                //alert(param);
                //return;

                $.ajax({
                    type: "POST",
                    url: "test.htm",
                    dataType: 'json',
                    data: param,
                    contentType: "application/json; charset=utf-8",
                    success: function(data) {
                        for(var i in data){
                            $("#display").append("<br/>"+data[i].XM);
                        }
//	    		
                    }, error: function(data) {
                        alert("数据无法解析");
                    }
                }).done(function() {

                });


            });
            
        </script>
        
    </body>
</html>
