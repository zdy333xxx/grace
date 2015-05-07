function arrayify( array ) {
  return [].slice.call( array );
}

function $( selector, context ) {
  return ( context || document ).querySelector( selector );
}

function $$( selector, context ) {
  return arrayify( (context || document).querySelectorAll(selector) );
}

function trigger( node, name, detail ) {
  var e = new CustomEvent( name, {detail: detail} );
  node.dispatchEvent( e );
}

var pfx = (function () {
  var style = document.createElement('dummy').style,
      prefixes = 'Webkit Moz O ms Khtml'.split(' '),
      memory = {};
  return function ( prop ) {
      if ( typeof memory[ prop ] === "undefined" ) {
          var ucProp  = prop.charAt(0).toUpperCase() + prop.substr(1),
              props   = (prop + ' ' + prefixes.join(ucProp + ' ') + ucProp).split(' ');
          memory[ prop ] = null;
          for ( var i in props ) {
              if ( style[ props[i] ] !== undefined ) {
                  memory[ prop ] = props[i];
                  break;
              }
          }
      }
      return memory[ prop ];
  };
})();

function css( elem, prop ) {
  if ( typeof prop === 'string' ) {
    if ( prop.indexOf( ':' ) > -1 ) return elem.style.cssText = prop;
    else {
      var style = window.getComputedStyle( elem );
      return style[ prop ];
    }
  }
  var key, pkey;
  for ( key in prop ) {
    if ( prop.hasOwnProperty(key) ) {
       pkey = pfx( key );
       if ( pkey ) {
         elem.style[ pkey ] = prop[ key ];
       }
    }
  }
}

function transition( dom, config ) {
  dom.offsetWidth; // re-layout.
  var style = {};
  var transform = '';
  for ( var key in config ) {
    switch( key ) {
      case 'rotate':
      case 'translate':
      case 'skew':
        transform += key + '(' + config[key] + ') ';
        break;
      default:
        style[ key ] = config[ key ];
    }
  }
  style.transform = transform;
  css( dom, style );
}

function getpage() {
  var hashlist = [
    'dashboard',
    'start',
    'data'
  ];
  var hash = location.hash.slice(1);
  hash = hash.toLowerCase();
  return hashlist.indexOf( hash ) > -1 ? hash : hashlist[2];
}

function tab( tabs, panels, callback, config ) {
  config = config || {
    effect: false,
    ids: []
  };
  var ids = config.ids;
  var context = config.context;
  callback = callback || function() {};
  tabs.forEach(function(tab,index) {
    tab.addEventListener('click', function(e) {
      // 只有点击事件时才发送请求.
      // 等 webkit 支持 e.isTrusted
      if ( e.detail === 1 ) {
        app.ping({
          navi: ids[ index ]
        });
      }
      if ( config.effect ) {
        context.style.marginLeft = -1 * index * 1000 + 'px';
      }
      else {
        panels.forEach(function( panel, i ) {
          panel.style.display = i === index ? '' : 'none';
        });
      }

      tabs.forEach(function( tab, i ) {
        var classList = tab.classList;
        classList[ i === index ? 'add' : 'remove' ]( 'current' );
      });

      callback( index );

      e.preventDefault();
    }, false);
  });
}

function zoomNumber( m, n, cb ) {
  var c = ( n - m ) * 0.04 + m;
  c = Math.ceil( c );
  cb( c );
  if ( c < n ) {
    setTimeout(function() {
      zoomNumber( c, n, cb );
    }, 16)
  }
}

// app.
var app = (function() {

  var ids = [ 'data', 'start', 'dashboard' ];

  var pages = ids.reduce(function(map, id) {
    return map[id] = $('#'+id), map;
  }, {});

  ids.forEach(function(id) {
    document.addEventListener( id + '_init', function(e) {
      app[ id ].init( e.detail );
    }, false );
  });

  function show( id ) {
    if ( ids.indexOf(id) > -1 ) {
      ids.forEach(function(page) {
        var node = pages[ page ];
        node.style.display = id === page ? '' : 'none';
        var method = id === page ? 'remove' : 'add';
        node.classList[ method ]( 'hide' );
      });
    }
  }

  function proxy( page, cb ) {
    trigger( document, page + '_init', {
      callback: function() {
        cb( page );
        pingback({
          page: page
        });
      }
    });
  }

  var navlist = $$( 'header li' );


  var hid;
  if ( localStorage.id ) {
    hid = localStorage.id;
  }
  else {
    hid = localStorage.id = (function() {
      var n = new Date().getTime();
      var c = escape(n*1000+Math.round(Math.random()*1000));
      return c;
    })();
  }
  var pingbackaddress = 'http://ping.ie.sogou.com/acc.gif';
  function pingback( param ) {
    var string = '?h=' + hid + '&';
    string += Object.keys( param ).map(function( key ) {
      return key + '=' + encodeURIComponent( param[key] );
    }).join( '&' );
    ( new Image() ).src = pingbackaddress + string;
  }

  var object = {
    init: function( page ) {
      var node, className = 'current';
      page = page || getpage();
      navlist.forEach(function(node) { node.classList.remove(className); });
      node = navlist[ page === 'dashboard' ? 1 : 0 ];
      node.classList.add( className );
      this.show( page );
    },
    show: function( page ) {
      proxy( page, show );
    },
    ping: pingback
  };

  window.addEventListener( 'hashchange', function(e) {
    object.init();
  }, false);

  window.addEventListener( 'popstate', function(e) {
    var state = e.state;
  }, false );

  navlist.forEach(function( node, index ) {
    node.dataset.index = index;
    var link = $( 'a', node );
    var className = 'current';
    link.addEventListener( 'click', function(e) {
      e.preventDefault();
      var hash = this.hash;
      var page = hash.slice(1);
      var href = location.href.replace( location.hash, '' );
      history.pushState({
        page: page
      }, document.title, href + hash );
      app.init( page );
      pingback({
        navi: page
      });
    }, false);
  });

  ids.forEach(function(id) {
    object[ id ] = {
      id: '#' + id
    };
  });

  return object;
})();

// @NOTE: only for test~
/*
  var _ACC = window.external.Acc;
  window.external.Acc = function( key, callback, argus, others ) {
    switch(key) {
      case 'GetConfig':
      case 'SetConfig':
      case 'GetTopAccDomains':
      case 'GetAvrageAccRate':
      case 'GetRecentAccUrl':
      case 'GetAccRate':
      case 'GetSampleUrls':
        _ACC.apply( window.external, arguments );
        break;
      case 'GetTopAccDomains':
        setTimeout(function() {
          window[ callback ](
            '',
            JSON.stringify([
              {
                site: '新浪微博',
                name: 'http://weibo.com/',
                acc_rate: 80
              },{
                site: '知乎',
                name: 'http://zhihu.com/',
                acc_rate: 30
              },{
                site: 'sohu',
                name: 'http://sohu.com/',
                acc_rate: 10
              },{
                site: '网易',
                name: 'http://163.com/',
                acc_rate: 90
              }
            ])
          );
        }, 0);
        break;
      case 'GetSampleUrls':
        setTimeout(function() {
          window[ callback ](
            '',
            JSON.stringify([
              { title: '搜狗浏览器', href: 'http://www.sogou.com/' },
              { title: '网易', href: 'http://163.com/' },
              { title: '新浪微博', href: 'http://www.weibo.com/' },
              { title: '知乎', href: 'http://www.zhihu.com/' }
            ])
          );
        }, 0);
        break;
      case 'GetAvrageAccRate':
        setTimeout(function() {
          window[ callback ](
            '',
            JSON.stringify({
              acc_rate: 35
            })
          );
        }, 0);
        break;
      case 'GetRecentAccUrl':
        setTimeout(function() {
          window[ callback ](
            '',
            JSON.stringify({
              name: 'http://www.zhihu.com/',
              acc_rate: 45
            })
          );
        }, 0);
        break;
      case 'GetAccRate':
        setTimeout(function() {
          window[ callback ](
            '',
            JSON.stringify( [] )
          );
        }, 0);
        break;
      default:
        break;
    }
  };
*/

var _ = (function() {
  var id = 0;
  var prefix = '_json_p_' + ('' + Math.random()).slice( 2, 8 );
  var noop = function() {};
  return function(cb) {
    var name = prefix + (++id);
    this[ name ] = function() {
      // console.log( 'callback:', arguments );
      var fn = typeof cb === 'function' ? cb : noop;
      fn.apply( null, [].slice.call(arguments, 1) );
    };
    return name;
  };
})();

app.api = function( key, callback ) {
  var argus = [ key, _(callback), '' ];
  var realArgus = [].slice.call( arguments, 2 );
  window.external.Acc.apply( window.external, argus.concat(realArgus) );
};

// 自定义接口.
app.userapi = function( key, callback, id ) {
  var argus = [ key, callback, id ];
  var realArgus = [].slice.call( arguments, 3 );
  window.external.Acc.apply( window.external, argus.concat(realArgus) );
}

app.response = function( appid, state ) {
  switch( appid ) {
    case 'Prefetch':
      var box = $( '#image' );
      var btn = $( 'button', box );
      var dataset = btn.dataset;
      var method = state == '1' ? 'remove' : 'add';
      box.classList[ method ]( 'disabled' );
      btn.classList[ method ]( 'disabled' );
      if ( dataset.state != state ) {
        app.dashboard.update( 'ImgAcc', state == '1');
      }      
      break;
    default:
    break;
  }
};

// dashboard 设置配置信息回调.
app.updateConfig = function( appid, status ) {
  if ( status === 'succeeded' ) {
    var appdom = $( '#appid_' + appid );
    var button = appdom.nextElementSibling;
    var dataset = button.dataset;
    var state = dataset.state;
    dataset.state = state === '1' ? '0' : '1';
    app.response( appid, dataset.state );
    var text = app.dashboard.info.text;
    var dom = $( '#' + dataset.id + '_state' );
    dom.classList.toggle( 'hide' );
    button.textContent = state === '1' ? text.open : text.close;
    trigger( document, 'prefetch-changed', dataset.state === '1' );
  }
};

document.addEventListener( 'update-prefetch-config', function(e) {
  if ( app.dashboard.loaded ) {
    app.updateConfig( 'Prefetch' );
  }
});

document.addEventListener( 'contextmenu', function(e) {
  e.preventDefault();
}, false);