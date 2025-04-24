// 缓存优化：使用内存缓存来减少重复请求
const memoryCache = {
  data: {},
  timeout: {},
  
  // 获取缓存数据
  get(key) {
    return this.data[key];
  },
  
  // 设置缓存数据
  set(key, value, expireSeconds = 300) { // 默认缓存5分钟
    this.data[key] = value;
    
    // 清除旧的超时
    if (this.timeout[key]) {
      clearTimeout(this.timeout[key]);
    }
    
    // 设置新的超时
    this.timeout[key] = setTimeout(() => {
      delete this.data[key];
      delete this.timeout[key];
    }, expireSeconds * 1000);
  },
  
  // 清除指定缓存
  clear(key) {
    delete this.data[key];
    if (this.timeout[key]) {
      clearTimeout(this.timeout[key]);
      delete this.timeout[key];
    }
  },
  
  // 清除所有缓存
  clearAll() {
    this.data = {};
    Object.keys(this.timeout).forEach(key => {
      clearTimeout(this.timeout[key]);
    });
    this.timeout = {};
  }
};

// 原始common对象
const common = {
    set(key,value){
        var curTime = new Date().getTime();
        localStorage.setItem(key,JSON.stringify({data:value,time:curTime}));
    },

    get(key){
        var data = localStorage.getItem(key);
        if(data==null){
            return null;
        }
        var dataObj = JSON.parse(data);
        if (new Date().getTime() - dataObj.time>1000*60*24) {
            console.log('信息已过期');
            localStorage.removeItem(key);
            return null;
        }else{
            var dataObjDatatoJson = dataObj.data;
            return dataObjDatatoJson;
        }
     },
    getUserInfo(key){
        var data = localStorage.getItem(key);
        if(data==null){
            return null;
        }
        var dataObj = JSON.parse(data);
        if (new Date().getTime() - dataObj.time>1000*60*24) {
            console.log('信息已过期');
            localStorage.removeItem(key);
            return null;
        }else{
            var dataObjDatatoJson = JSON.parse(dataObj.data);
            return dataObjDatatoJson;
        }
    },

    remove(key) {
        localStorage.removeItem(key);
    },

    initRun() {
        /*
        * set 存储方法
        * @ param {String} 	key 键
        * @ param {String} 	value 值，存储的值可能是数组/对象，不能直接存储，需要转换 JSON.stringify
        * @ param {String} 	expired 过期时间，以分钟为单位
        * @ 由@IT·平头哥联盟-首席填坑官∙苏南 分享
        */
        const reg = new RegExp("__expires__");
        let data = this.source;
        let list = Object.keys(data);
        if(list.length > 0){
            list.map((key,v)=>{
                if( !reg.test(key )){
                    let now = Date.now();
                    let expires = data[`${key}__expires__`]||Date.now+1;
                    if (now >= expires ) {
                        this.remove(key);
                    };
                };
                return key;
            });
        };
    },
    
    // 添加内存缓存支持
    cache: memoryCache
}

// 导出对象
export default common;
