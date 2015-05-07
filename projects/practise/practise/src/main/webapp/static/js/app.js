app.dashboard.info = {
  'text': {
    open: '开启',
    close: '关闭'
  },
  'image': {
    id: 'image',
    appid: 'ImgAcc',
    support: true,
    title: '图片加速',
    state: 0,
    tips: '请先开启网页加速',
    description: '由搜狗预取引擎驱动，专门为淘宝、天猫等购物网站推出的图片加速技术，能大幅提升图片打开的速度。'
  },
  'taobao': {
    id: 'taobao',
    appid: 'taobaoacc',
    link: 'http://ie.sogou.com/app/app_4275.html',
    linkText: '查看',
    support: false,
    title: '淘宝加速',
    state: 0,
    description: '为淘宝、天猫、京东专门开辟网络通道，可以帮助用户突破办公室封锁，正常访问各大购物网站。'
  },
  'page': {
    id: 'page',
    appid: 'Prefetch',
    support: true,
    title: '网页加速',
    state: 0,
    description: '由搜狗预取引擎驱动，精准预测你即将访问的网页，提前加载资源，平均提速一倍以上，真正实现网页秒开！'
  },
  'flash': {
    id: 'flash',
    appid: 'FlashAcc',
    support: true,
    title: 'Flash加速',
    state: 1,
    description: '专门为游戏玩家和视频看客推出的加速技术，有效减少Flash的卡顿现象，保证畅快淋漓的体验。'
  },
  'edu': {
    id: 'edu',
    appid: 'CernetAcc',
    support: true,
    title: '教育网加速',
    state: 1,
    description: '全面提升教育网用户访问外网的速度，搜狗高速浏览器奠基之作。'
  },
  'download': {
    id: 'download',
    appid: 'SEDownload',
    support: true,
    title: '高速下载',
    state: 1,
    description: '基于P2SP技术，通过多线程以及网络层优化，充分发挥带宽潜力，媲美专业下载软件的下载速度。'
  },
  'protect': {
    id: 'protect',
    appid: 'AssureNetSpeed',
    support: true,
    title: '网速保护',
    state: 1,
    description: '在您下载软件时为您预留足够的带宽，保证您能正常浏览网页。'
  },
  'process': {
    id: 'process',
    appid: '_process',
    support: false,
    title: '完美防假死',
    description: '完美防假死技术，某个页面出错崩溃或者失去响应时，可以直接关闭，不会影响其他页面的正常浏览。'
  },
  'webkit': {
    id: 'webkit',
    appid: '_webkit',
    support: false,
    title: '高速模式',
    description: '采用最快的Chromium内核，不断自主优化，让用户享受最高速的体验。'
  },
  'webpage': {
    id: 'webpage',
    appid: '_webpage',
    support: false,
    title: '网页预渲染',
    description: '通过用户数据挖掘，精准预测用户即将访问的网页，提前渲染，能瞬间打开网页！'
  }
};

app.dashboard.render = function( result ) {
  var list = [ 
    'page', 
    'image',
    'taobao',
    'flash', 
    'edu', 
    'download', 
    'protect', 
    'webpage',
    'process', 
    'webkit' 
  ];
  var info = this.info;
  var text = info.text;
  var required = {
    image: info.page.appid
  };
  var texts = Object.keys( text );
  var box = $( this.id );
  var tmplid = '#tmpl-dashboard';
  var tmpl = $(tmplid).innerHTML;
  var html = rt.template( tmpl, list.map(function( key ) {
    var ret = info[ key ];
    ret.state = +(result[ ret.appid ] === 'true');
    texts.forEach(function(t) {
      ret[t] = text[t];
    });
    if ( required[key] ) {
      ret.require = result[required[key]] === 'true';
    }
    return ret;
  }));
  box.innerHTML = html;
};

app.dashboard.update = function( appid, state ) {
  app.userapi( 'SetConfig', 'app.updateConfig', appid, appid, '' + state );
};

app.dashboard.init = function( detail ) {
  detail.callback();
  if ( this.loaded ) {
    return;
  }
  this.loaded = 1;
  var self = app.dashboard;
  app.api( 'GetConfig', function( string ) {
    var result = {};
    try {
      result = JSON.parse( string );
    } catch(e) {
      console.log( e );
    }
    self.render( result );
  }, 'all' );

  $( this.id ).addEventListener( 'click', function(e) {
    var target = e.target;
    if ( target.classList.contains( 'disabled' ) ) {
      return;
    }
    var dataset = target.dataset;
    var appid = dataset.appid || '';
    var nodename = target.nodeName.toLowerCase();
    if ( nodename === 'button' ) {
      var state = +dataset.state;
      app.dashboard.update( appid, !state );
      app.ping({
        button: appid.toLowerCase(),
        state: Number(!state)
      });
      e.preventDefault();
    } else if ( nodename === 'a' ) {
      app.ping({
        button: appid.toLowerCase(),
        state: 1
      })
    }
  }, false );
};

app.start.callback = function( list ) {
  var wrap = $( this.id );
  var bd = $( '.bd', wrap );

  var tmpl = $( '#tmpl-start' ).innerHTML;
  bd.innerHTML = rt.template( tmpl, list );

  if ( list.length > 0 ) {
    bd.style.visibility = 'visible';
    list.length < 3 && bd.classList.add( 'startfix' );
  }
  else {
    bd.classList.add( 'nodata' );
  }

};

app.start.webpage = function( is ) {
  var go = $( '#go' );
  go.dataset.is = is;
  var text = {
    open: '已启用网页加速器',
    close: '启动网页加速器'
  };
  if ( is ) {
    go.textContent = text.open;
  }
  else {
    go.textContent = text.close;
    go.classList.add( 'button' );
  }

  function update( is ) {
    go.classList[ is ? 'remove' : 'add' ]( 'button' );
    go.textContent = is ? text.open : text.close;
    go.dataset.is = is;
  }

  document.addEventListener( 'prefetch-changed', function(e) {
    update( e.detail );
  }, false);

  go.addEventListener( 'click', function(e) {
    if ( this.dataset.is === 'false' ) {
      app.api( 'SetConfig', function(result) {
        if ( result === 'succeeded' ) {
          update( true );
          trigger( document, 'update-prefetch-config' );
        }
      }, 'Prefetch', String(true) );
    }
    e.preventDefault();
  }, false );
};

app.start.init = function( detail ) {
  if ( this.loaded ) {
    return detail.callback();
  }
  this.loaded = 1;
  // 读取是否开启加速器.
  app.api( 'GetConfig', function( string ) {
    var result = {};
    try {
      result = JSON.parse( string );
    } catch(e) {
      console.log( e );
    }
    app.start.webpage( result.Prefetch === 'true' );
  }, 'Prefetch');

  // 读四条数据.
  app.api( 'GetSampleUrls', function( string ) {
    var result = [];
    try {
      result = JSON.parse( string );
    } catch(e) {
      console.log(e);
    }
    app.start.callback( result.slice(0,4) );
  });

  if ( detail && detail.callback ) {
    detail.callback();
  }
};


app.data.tab = function( index ) {
  var tabs = $$( '#data-nav a');
  var panels = $$( '.data-wrap .panel' );
  tab( tabs, panels, function( index ) {
    // localStorage.data_index = index;
    var funcs = [ app.data.start, app.data.sites, function() {} ]
    funcs[ index ]();
  }, {
    context: $( '#data .data-wrap' ),
    effect: true,
    ids: [ 'top', 'sites', 'days' ]
  });
  tabs[index].click();
  // sites
  var stabs = $$( '#sites-nav a' );
  var spanels = $$( '#sites-bd > .panel' );
  tab( stabs, spanels, function() {
    app.data.sites();
  }, {
    ids: [ 'speed', 'time' ]
  });
  stabs[0].click();
};

app.data.start = function( value ) {
  var average = $( '#data-average-acc' ),
      round = $( '#round' );
  if ( value ) average.dataset.value = value;
  value = value || average.dataset.value || 100;

  zoomNumber( 0, value, function(c) {
    average.textContent = c + '%';
  });

  transition( round, {
    rotate: '130deg',
    transition: ''
  });

  setTimeout(function() {
    transition( round, {
      rotate: value % 360 + 360 + 130 + 'deg',
      transition: 'all 1s ease'
    });
  }, 0);

};

app.data.renderStart = function( object ) {
  var tmpl = $( '#tmpl-info' ).innerHTML;
  $( '#data-start' ).innerHTML = rt.template( tmpl, object );
  app.data.start( object.average.acc_rate );
};

app.data.sites = function() {
  [ '#sites-speed-panel', '#sites-time-panel' ].forEach(function( item ) {
    var box = $( item );
    var spans = $$( 'span', box );
    spans.forEach(function( span ) {
      span.style.cssText = '';
    });
    setTimeout(function() {
      spans.forEach(function( span ) {
        var style = span.dataset.style;
        span.style.borderWidth;
        if ( style ) {
          transition( span, {
            cssText: style
          });
        }
      });
    }, 10);
  });
};

app.data.renderSites = function( list ) {
  var tmpl = $( '#tmpl-sites' ).innerHTML;
  var speeddom = $( '#sites-speed-panel' );
  list = list.filter(function( item ) {
    return item.domain && item.domain.length > 0;
  });

  // 取可用数据 + 修正最大值.
  var datalist = list.slice( 0, 4 );
  var base = datalist.reduce(function( max, item ) {
    return max >= item.acc_rate ? max : item.acc_rate;
  }, base );
  base *= 1.2;
  // 根据最大值, 修正数据.
  datalist = datalist.map(function( item ) {
    item.acc = 100 * item.acc_rate / base;
    item.acc_rate2 = item.acc_rate;
    return item;
  });
  speeddom.innerHTML = rt.template( tmpl, {
    type: 'speed',
    list: datalist
  });

  datalist = list.slice( 0, 4 ).map(function( item ) {
    item.acc = ( item.acc_rate/100/(item.acc_rate/100+1) ) * 100;
    item.acc_rate2 = item.acc.toFixed(2);
    return item;
  });

  var timedom = $( '#sites-time-panel' );
  timedom.innerHTML = rt.template( tmpl, {
    type: 'time',
    list: datalist
  });

  app.data.sites();
};

app.data.renderDays = function( list ) {
  var layer = $( '#acc' );
  var style;
  switch( list.length ) {
    case 0:
      return layer.style.display = 'none';
      break;
    case 1:
      style = 'circle';
      break;
    default:
      style = 'none';
      break;
  }
  layer.style.display = '';
  var chart = echarts.init( layer );
  var option = {
      // 线条颜色.
      color: [  'white'  ],
      title: {
        text: '| 查看网页加速成功率随使用时间的变化',
        x: 78,
        y: 0,
        textStyle: {
          color: 'rgba( 255, 255, 255, 1 )',
          'fontSize': 14,
          'fontFamily': '微软雅黑',
          'fontWeight': 'normal'
        }
      },
      tooltip : {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 2545, 255, 0)',
        textStyle: {
          color: '#fff',
          fontFamily: '微软雅黑'
        },
        formatter: function( param, ticket, callback ) {
          var list = param[0], message = '';
          if ( list[1] && list[2] != null ) {
            message = list[0] + "<br />" + list[1] + ': ' + list[2] + '%';
          }
          return message;
        },
        axisPointer: {
          type: 'line',
          lineStyle: {
            color: 'rgba(255, 255, 255, 0.5)',
            width: 1
          }
        }
      },
      grid: {
        x: 80,
        y: 35,
        x2: 30,
        y2: 60,
        borderWidth: 0
      },
      axis: {
        min: 0
      },
      xAxis : [{
        axisTick: {
          show: false
        },
        type : 'category',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#fff',
            width: 2
          },
        },
        axisLabel: {
          textStyle: {
            color: 'rgba(255, 255, 255, 0.5)'
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: [ 'rgba(255,255,255,0.1)' ]
          }
        },
        boundaryGap: false,
        data: function (){
          var datalist = [];
          list.forEach(function( item ) {
            datalist.push( item.date );
          });
          return datalist.reverse();
        }()
      }],
      yAxis : [
        {
          type : 'value',
          axisLine: {
            show: true,
            lineStyle: {
              color: 'white',
              width: 2
            },
          },
          axisLabel: {
            textStyle: {
              color: 'rgba(255, 255, 255, 0.5)'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: [ 'rgba(255,255,255,0.1)' ]
            }
          },
        }
      ],
      series : [{
        name:'加速成功率',
        type:'line',
        symbol: style,
        large: true,
        itemStyle: {
          normal: {
            areaStyle: {
              type: 'default',
              color: 'rgba(22, 201, 241, 0.2)'
            }
          }
        },
        data: function (){
            var datalist = [];
            list.forEach(function(item) {
              datalist.push( item.acc_rate );
            });
            return datalist.reverse();
        } ()
      }
    ]
  };
  chart.setOption( option );
};

app.data.init = function( detail ) {
  app.api( 'GetTopAccDomains', function( string ) {
    var list = [];
    try {
      list = JSON.parse( string );
    } catch (e) {
      console.log( e );
    }
    list = list.filter(function( item ) {
      return item.acc_rate > 0;
    });
    if ( list.length === 0 ) {
      return app.show( 'start' );
    }

    var map = {
      top: list[0]
    };

    var start = function() {
      app.data.renderStart( map );
      app.data.tab( 0 ); // localStorage.data_index ||
      if ( typeof detail.callback === 'function' ) {
        detail.callback();
      }
    };

    app.api( 'GetAvrageAccRate', function( string ) {
      try {
        map.average = {
          acc_rate: JSON.parse( string )
        };
        if ( map.recent ) {
          start();
        }
      } catch(e) {
        console.log( e, string );
      }
    });

    app.api( 'GetRecentAccUrl', function( string ) {
      try {
        map.recent = JSON.parse( string );
        if ( map.average ) {
          start();
        }
      } catch(e) {
        console.log( e );
      }
    });

    app.data.renderSites( list );
  });
  app.api( 'GetAccRate', function( string ) {
    var list = [];
    try {
      list = JSON.parse( string );
    } catch(e) {
      console.log(e);
    }
    app.data.renderDays( list );
  }, 180);
};

app.init();
