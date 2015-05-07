<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

        <%@include file="bootstrap-base.jsp" %>

        <link rel="stylesheet" href="static/css/home.css" />
        <script src="static/js/home.js"></script>

        <title>Home Page</title>

    </head>

    <body>


        <nav class="navbar navbar-default" role="navigation">

            <div  class="container-fluid">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Slience</a>
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="#">Service</a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Document <span class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="#">Literature</a></li>
                                <li><a href="#">Science</a></li>  
                                <li class="divider"></li>
                                <li><a href="#">Something else here</a></li>
                            </ul>
                        </li>
                        <li><a href="#">Tool</a></li>
                        <li><a href="#">Demo</a></li>
                        <li><a href="#">Storage</a></li>
                        <li><a href="#">Dairy</a></li>
                    </ul>

                    <!--
                    <form class="navbar-form navbar-left" role="search">
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="Search">
                        </div>
                        <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                    -->

                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="#">Link</a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Other <span class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="#">Action</a></li>
                                <li><a href="#">Another action</a></li>
                                <li><a href="#">Something else here</a></li>
                                <li class="divider"></li>
                                <li><a href="#">Separated link</a></li>
                            </ul>
                        </li>
                    </ul>
                </div><!-- /.navbar-collapse -->
            </div><!-- /.container-fluid -->

        </nav>


        <div  class="container-fluid" style="height: 40px"></div>
        
        <!-- Page Content -->

        <div id="home_content" class="container-fluid">

            <div id="center_content" class="row">
                <div id="func_category" class="col-md-2">

                    <ul id="collNameList" class="list-group">
                        <!--
                        <li class="list-group-item">category 1st</li>
                        <li class="list-group-item">category 2st</li>
                        <li class="list-group-item active">category 3st</li>
                        <li class="list-group-item">category 4st</li>
                        <li class="list-group-item">category 5st</li>
                        <li class="list-group-item">category 6st</li>
                        <li class="list-group-item">category 7st</li>
                        <li class="list-group-item">category 8st</li>
                        <li class="list-group-item">category 9st</li>
                        -->
                    </ul>

                </div><!-- /.func_category -->

                <div class="col-md-10" style=" background-color: #99cc33">


                </div>

            </div><!-- /.center_content -->



        </div><!-- /.home_content -->


    </body>
</html>
