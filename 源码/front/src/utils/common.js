export default {


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
        const data = this.source,
            value = data[key];
        delete data[key];
        delete data[`${key}__expires__`];
        return value;
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
    }
}
