var w = window.innerWidth
        || document.documentElement.clientWidth
        || document.body.clientWidth;

var h = window.innerHeight
        || document.documentElement.clientHeight
        || document.body.clientHeight;

var aw = screen.availWidth;//可用的屏幕宽度
var ah = screen.availHeight;// 可用的屏幕高度

$(document).ready(function() {
    //var navH =$(".navbar").outerHeight(true);

    //初始化内容区域高度，填满页面
    var homeContentH = h - 80;
    $("#func_category").css({
        height: homeContentH + 'px'
    });



    //初始化左侧功能列表
    var localInfoParam = {
    };

    var param = JSON.stringify(localInfoParam).toString();

    //alert(param);
    //return;
     
    $.ajax({
        type: "POST",
        url: "hello.htm",
        dataType: 'json',
        data: param,
        contentType: "application/json; charset=utf-8",
        success: function(data) {
    
            for (var i in data) {
                var collNameItemHtml = '<li title="'+data[i]+'"'+' class="list-group-item">' + data[i] + '</li>';
                $("#collNameList").append(collNameItemHtml);
            }
//	    		
        }, error: function(data) {
            alert("数据无法解析");
        }
    }).done(function() {

    });



});// end document-ready