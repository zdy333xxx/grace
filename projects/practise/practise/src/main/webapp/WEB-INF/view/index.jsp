<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

        <title>Index page</title>

        <link rel="stylesheet" href="static/css/base.css">
        <link rel="stylesheet" href="static/css/app.css">

        

        <style>
            header, section, footer, aside, nav, main, article, figure {
                display: block; 
            }

        </style>        
    </head>

    <body>
<header>
  <!-- @NOTE: 浏览器开发要求, 指定到文件名 -->
  <h1><a href="./index.html">搜狗加速器</a></h1>
  <nav>
    <ul>
      <li><a href="#data">数据中心</a></li>
      <li><a href="#dashboard">加速器管理</a></li>
    </ul>
  </nav>
</header>
<div class="wrap">


<section id="start">
  <div class="hd">
    <h1>搜狗网页加速器</h1>
    <p>由搜狗预取引擎驱动，通过大数据计算和个性化预测，提前下载到本地，平均提速200-400%</p>
    <button id="go" class="go">已启用网页加速器</button>
  </div>
  <div class="bd"></div>
</section>



<section id="dashboard" class="hide"></section>


<section id="data" class="hide">
  <div class="hd">
    <ul class="mini-nav data-hd" id="data-nav">
      <li><a class="current">加速信息</a></li>
      <li><a>加速网站</a></li>
      <li><a>每日加速</a></li>
    </ul>
  </div>
  <div class="data-helper">
    <div class="data-wrap">
      <section class="panel info">
        <div class="data-bd bd" id="data-start"></div>
        <div class="ft">
          <h1>网页加速信息</h1>
          <p>由搜狗预取引擎驱动，通过大数据计算和个性化预测，<br />提前下载到本地，平均提速200-400%。</p>
        </div>
      </section>
      <section class="panel sites">
        <div class="data-bd bd" id="sites-bd">
          <div class="tab">
            <span>| 查看不同网站的提速效果</span>
            <ul class="mini-nav" id="sites-nav">
              <li><a class="current">提速百分比</a></li>
              <li><a>节省时间比</a></li>
            </ul>
          </div>
          
          <div class="panel" id="sites-speed-panel"></div>
          <div class="panel" id="sites-time-panel"></div>
        </div>
        <div class="ft">
          <h1>最常访问的网站TOP4</h1>
          <p>搜狗加速器会根据页面资源选择不同的技术进行加速，<br />不同的网站加速效果也不同。</p>
        </div>
      </section>
      <section class="panel day">
        <div class="data-bd bd">
          <div id="acc" style="width: 880px;height: 380px;margin: 0 auto;"></div>
        </div>
        <div class="ft">
          <h1>每日网页加速成功率</h1>
          <p>预取引擎会通过个人数据挖掘，预测你即将访问的网页。</p>
        </div>
      </section>
    </div>
  </div>
</section>
</div>
<script src="static/js/rt.0.5.0.min.js"></script>
<script src="static/js/echarts-se.js"></script>
<script src="static/js/base.js"></script>
<script src="static/js/app.js"></script>

</body>
</html>