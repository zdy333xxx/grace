<%-- 
    Document   : upload
    Created on : 2015-2-26, 21:18:17
    Author     : zdy
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Upload Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>

        <form action="uploadFile" enctype="multipart/form-data" method="POST">
            <p>id: <input name="pid" type="text" value="1"/></p>
            <p>name: <input name="pname" type="text" value="表名称"/></p>
            <p>id: <input name="myfile" type="file"/></p>
            <p><button type="submit">提交</button></p>
        </form>

    </body>
</html>
