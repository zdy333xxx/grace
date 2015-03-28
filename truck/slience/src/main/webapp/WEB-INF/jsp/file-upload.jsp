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
        <br />
        <form method="POST" enctype="multipart/form-data" action="picture/upload">
            File to upload: <input type="file" name="file">
            <br />
            <br />
            Name: <input type="text" name="name">
            <br />
            <br />
            <input type="submit" value="Upload"> Press here to upload the file!
        </form>
    </body>

</html>
