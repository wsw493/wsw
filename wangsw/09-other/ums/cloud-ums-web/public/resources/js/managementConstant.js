var ManagementConstant = {
    //调用后台接口url
    // URL_Table: 'http://192.168.1.211:8082',
    // URL_Table: 'http://192.168.1.248:9002',
    URL_Table: '',
	//地图类型常量
	VortexMapType: [
		{
            text:'arcgis',
            value:'ARCGIS'
        },{
            text:'aMap',
            value:'AMAP'
        },{
            text:'bMap',
            value:'BMAP'
        }
	],
    //后台接口后缀
    back_dynamic_suffix: '.sa',
    back_dynamic_suffix_smvc: '.smvc',
    permission_suffix_read: '.read'
}
//ajax访问数据格式
var ContentType = {
    JSON: 'application/json;charset=UTF-8',
    String: 'application/x-www-form-urlencoded'
}
var MapType = [
    {
        mapType: 'bmap',
        mapName: '百度地图',
        coordinate:'bd09'
    },
    {
        mapType: 'amap',
        mapName: '高德地图',
        coordinate:'gcj02'
    },
    {
        mapType: 'tmap',
        mapName: '天地图',
        coordinate:'wgs84'
    },
    {
        mapType: 'gmap',
        mapName: 'arcgis',
        coordinate:''
    }
]
//开发期间测试所用systemId
var test_systemId = '547f80e2c8fa4c06a70d9e02cccdc3b1';