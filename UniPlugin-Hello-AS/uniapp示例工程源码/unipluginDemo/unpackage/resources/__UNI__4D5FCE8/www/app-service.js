(this["webpackJsonp"]=this["webpackJsonp"]||[]).push([["app-service"],{"0de9":function(t,e,n){"use strict";function i(t){var e=Object.prototype.toString.call(t);return e.substring(8,e.length-1)}function r(){return"string"===typeof __channelId__&&__channelId__}function a(t){for(var e=arguments.length,n=new Array(e>1?e-1:0),i=1;i<e;i++)n[i-1]=arguments[i];console[t].apply(console,n)}function o(){for(var t=arguments.length,e=new Array(t),n=0;n<t;n++)e[n]=arguments[n];var a=e.shift();if(r())return e.push(e.pop().replace("at ","uni-app:///")),console[a].apply(console,e);var o=e.map((function(t){var e=Object.prototype.toString.call(t).toLowerCase();if("[object object]"===e||"[object array]"===e)try{t="---BEGIN:JSON---"+JSON.stringify(t)+"---END:JSON---"}catch(r){t="[object object]"}else if(null===t)t="---NULL---";else if(void 0===t)t="---UNDEFINED---";else{var n=i(t).toUpperCase();t="NUMBER"===n||"BOOLEAN"===n?"---BEGIN:"+n+"---"+t+"---END:"+n+"---":String(t)}return t})),s="";if(o.length>1){var c=o.pop();s=o.join("---COMMA---"),0===c.indexOf(" at ")?s+=c:s+="---COMMA---"+c}else s=o[0];console[a](s)}n.r(e),n.d(e,"log",(function(){return a})),n.d(e,"default",(function(){return o}))},"0f2e":function(t,e,n){"use strict";n.r(e);var i=n("5a17"),r=n.n(i);for(var a in i)"default"!==a&&function(t){n.d(e,t,(function(){return i[t]}))}(a);e["default"]=r.a},1192:function(t,e,n){"use strict";n.r(e);var i=n("4d48"),r=n("3ebe");for(var a in r)"default"!==a&&function(t){n.d(e,t,(function(){return r[t]}))}(a);var o,s=n("f0c5"),c=Object(s["a"])(r["default"],i["b"],i["c"],!1,null,null,null,!1,i["a"],o);e["default"]=c.exports},"244d":function(t,e,n){"use strict";n("8c5d");var i=a(n("8bbf")),r=a(n("bacd"));function a(t){return t&&t.__esModule?t:{default:t}}function o(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(t);e&&(i=i.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,i)}return n}function s(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?o(Object(n),!0).forEach((function(e){c(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function c(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}i.default.config.productionTip=!1,r.default.mpType="app";var u=new i.default(s({},r.default));u.$mount()},"3ebe":function(t,e,n){"use strict";n.r(e);var i=n("5360"),r=n.n(i);for(var a in i)"default"!==a&&function(t){n.d(e,t,(function(){return i[t]}))}(a);e["default"]=r.a},"4d48":function(t,e,n){"use strict";var i,r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("view",{staticClass:t._$s(0,"sc","button-sp-area"),attrs:{_i:0}},[n("button",{attrs:{_i:1},on:{click:function(e){return t.showRichAlert()}}})])},a=[];n.d(e,"b",(function(){return r})),n.d(e,"c",(function(){return a})),n.d(e,"a",(function(){return i}))},5360:function(t,e,n){"use strict";(function(t){Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0;var n=uni.requireNativePlugin("modal"),i=uni.requireNativePlugin("DCloud-RichAlert"),r={data:function(){return{title:""}},onLoad:function(){},methods:{showRichAlert:function(){i.show({position:"bottom",title:"提示信息",titleColor:"#FF0000",content:"<a href='https://uniapp.dcloud.io/' value='Hello uni-app'>uni-app</a> 是一个使用 Vue.js 开发跨平台应用的前端框架!\n免费的\n免费的\n免费的\n重要的事情说三遍",contentAlign:"left",checkBox:{title:"不再提示",isSelected:!0},buttons:[{title:"取消"},{title:"否"},{title:"确认",titleColor:"#3F51B5"}]},(function(e){var i=JSON.stringify(e);switch(n.toast({message:i,duration:1.5}),e.type){case"button":t("log","callback---button--"+e.index," at pages/sample/richAlert.vue:50");break;case"checkBox":t("log","callback---checkBox--"+e.isSelected," at pages/sample/richAlert.vue:53");break;case"a":t("log","callback---a--"+JSON.stringify(e)," at pages/sample/richAlert.vue:56");break;case"backCancel":t("log","callback---backCancel--"," at pages/sample/richAlert.vue:59");break}}))}}};e.default=r}).call(this,n("0de9")["default"])},"5a17":function(t,e,n){"use strict";(function(t){Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0;var n={onLaunch:function(){t("log","App Launch"," at App.vue:4")},onShow:function(){t("log","App Show"," at App.vue:7")},onHide:function(){t("log","App Hide"," at App.vue:10")}};e.default=n}).call(this,n("0de9")["default"])},"8bbf":function(t,e){t.exports=Vue},"8c5d":function(t,e,n){"undefined"===typeof Promise||Promise.prototype.finally||(Promise.prototype.finally=function(t){var e=this.constructor;return this.then((function(n){return e.resolve(t()).then((function(){return n}))}),(function(n){return e.resolve(t()).then((function(){throw n}))}))}),uni.restoreGlobal&&uni.restoreGlobal(weex,plus,setTimeout,clearTimeout,setInterval,clearInterval),__definePage("pages/index/index",(function(){return Vue.extend(n("9a9a").default)})),__definePage("pages/sample/richAlert",(function(){return Vue.extend(n("1192").default)}))},"9a9a":function(t,e,n){"use strict";n.r(e);var i=n("b494"),r=n("cba9");for(var a in r)"default"!==a&&function(t){n.d(e,t,(function(){return r[t]}))}(a);var o,s=n("f0c5"),c=Object(s["a"])(r["default"],i["b"],i["c"],!1,null,null,null,!1,i["a"],o);e["default"]=c.exports},a0b0:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0;var i={data:function(){return{list:[{id:"ext-module",name:"扩展 module",open:!1,url:"/pages/sample/ext-module"},{id:"ext-component",name:"扩展 component",open:!1,url:"/pages/sample/ext-component"},{id:"richAlert",name:"插件示例RichAlert",open:!1,url:"/pages/sample/richAlert"},{id:"hikvideo",name:"插件实例hikvideo",open:!1,url:"/pages/sample/hikvideo"}],navigateFlag:!1}},onLoad:function(){},methods:{triggerCollapse:function(t){if(this.list[t].pages)for(var e=0;e<this.list.length;++e)this.list[e].open=t===e&&!this.list[t].open;else this.goDetailPage(this.list[t].url)},goDetailPage:function(t){var e=this;if(!this.navigateFlag)return this.navigateFlag=!0,uni.navigateTo({url:t}),setTimeout((function(){e.navigateFlag=!1}),200),!1}}};e.default=i},b494:function(t,e,n){"use strict";var i,r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("view",{staticClass:t._$s(0,"sc","uni-container"),attrs:{_i:0}},[n("view",{staticClass:t._$s(1,"sc","uni-hello-text"),attrs:{_i:1}},[n("text",{staticClass:t._$s(2,"sc","hello-text"),attrs:{_i:2}})]),t._l(t._$s(3,"f",{forItems:t.list}),(function(e,i,r,a){return n("view",{key:t._$s(3,"f",{forIndex:r,key:e.id}),staticClass:t._$s("3-"+a,"sc","uni-panel"),attrs:{_i:"3-"+a}},[n("view",{staticClass:t._$s("4-"+a,"sc","uni-panel-h"),class:t._$s("4-"+a,"c",e.open?"uni-panel-h-on":""),attrs:{_i:"4-"+a},on:{click:function(e){return t.triggerCollapse(i)}}},[n("text",{staticClass:t._$s("5-"+a,"sc","uni-panel-text"),attrs:{_i:"5-"+a}},[t._v(t._$s("5-"+a,"t0-0",t._s(e.name)))]),n("text",{staticClass:t._$s("6-"+a,"sc","uni-panel-icon uni-icon"),class:t._$s("6-"+a,"c",e.open?"uni-panel-icon-on":""),attrs:{_i:"6-"+a}},[t._v(t._$s("6-"+a,"t0-0",t._s(e.pages?"":"")))])]),t._$s("7-"+a,"i",e.open)?n("view",{staticClass:t._$s("7-"+a,"sc","uni-panel-c"),attrs:{_i:"7-"+a}},t._l(t._$s("8-"+a,"f",{forItems:e.pages}),(function(e,i,r,o){return n("view",{key:t._$s("8-"+a,"f",{forIndex:r,key:i}),staticClass:t._$s("8-"+a+"-"+o,"sc","uni-navigate-item"),attrs:{_i:"8-"+a+"-"+o},on:{click:function(n){return t.goDetailPage(e.url)}}},[n("text",{staticClass:t._$s("9-"+a+"-"+o,"sc","uni-navigate-text"),attrs:{_i:"9-"+a+"-"+o}},[t._v(t._$s("9-"+a+"-"+o,"t0-0",t._s(e.name?e.name:e)))]),n("text",{staticClass:t._$s("10-"+a+"-"+o,"sc","uni-navigate-icon uni-icon"),attrs:{_i:"10-"+a+"-"+o}})])})),0):t._e()])}))],2)},a=[];n.d(e,"b",(function(){return r})),n.d(e,"c",(function(){return a})),n.d(e,"a",(function(){return i}))},bacd:function(t,e,n){"use strict";n.r(e);var i=n("0f2e");for(var r in i)"default"!==r&&function(t){n.d(e,t,(function(){return i[t]}))}(r);var a,o,s,c,u=n("f0c5"),l=Object(u["a"])(i["default"],a,o,!1,null,null,null,!1,s,c);e["default"]=l.exports},cba9:function(t,e,n){"use strict";n.r(e);var i=n("a0b0"),r=n.n(i);for(var a in i)"default"!==a&&function(t){n.d(e,t,(function(){return i[t]}))}(a);e["default"]=r.a},f0c5:function(t,e,n){"use strict";function i(t,e,n,i,r,a,o,s,c,u){var l,f="function"===typeof t?t.options:t;if(c){f.components||(f.components={});var p=Object.prototype.hasOwnProperty;for(var d in c)p.call(c,d)&&!p.call(f.components,d)&&(f.components[d]=c[d])}if(u&&((u.beforeCreate||(u.beforeCreate=[])).unshift((function(){this[u.__module]=this})),(f.mixins||(f.mixins=[])).push(u)),e&&(f.render=e,f.staticRenderFns=n,f._compiled=!0),i&&(f.functional=!0),a&&(f._scopeId="data-v-"+a),o?(l=function(t){t=t||this.$vnode&&this.$vnode.ssrContext||this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext,t||"undefined"===typeof __VUE_SSR_CONTEXT__||(t=__VUE_SSR_CONTEXT__),r&&r.call(this,t),t&&t._registeredComponents&&t._registeredComponents.add(o)},f._ssrRegister=l):r&&(l=s?function(){r.call(this,this.$root.$options.shadowRoot)}:r),l)if(f.functional){f._injectStyles=l;var v=f.render;f.render=function(t,e){return l.call(e),v(t,e)}}else{var _=f.beforeCreate;f.beforeCreate=_?[].concat(_,l):[l]}return{exports:t,options:f}}n.d(e,"a",(function(){return i}))}},[["244d","app-config"]]]);