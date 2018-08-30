require.config({
    waitSeconds:0,
    paths: {
        "jquery": "./jquery.min",
        "event": "./event",
        "gisMapConstant": "./gisMapConstant",
        "mapConstants":"./mapConstants",
        "MapUtil":"./MapUtil",
        // 百度地图
        "BaiDuMap":"http://api.map.baidu.com/getscript?v=2.0&ak=EVlFc6DZzAzU5avIjoxNcFgQ",
        // 地图相关插件: http://lbsyun.baidu.com/index.php?title=open/library
        "DistanceTool":"./mapPlugin/DistanceTool_min",
        "InfoBox":"./mapPlugin/InfoBox_min",
        "TextIconOverlay":"./mapPlugin/TextIconOverlay_min",
        "MarkerClusterer":"./mapPlugin/MarkerClusterer_min",
        "DrawingManager":"./mapPlugin/DrawingManager_min",
        "GeoUtils":"./mapPlugin/GeoUtils_min",
        "Heatmap":"./mapPlugin/Heatmap_min",
        "TrafficControl":"./mapPlugin/TrafficControl_min",
        // 封装的地图
        "vortexBMap":"./vortexBMap",
        // 主程序入口
        "app":"../../index",
        "mConstant":"./managementConstant",
        "downloadJS":"./download",
        "jqueryForm":"./jquery.form",
        "md5":"./md5",
        "sha":"./sha",
        "polyfill":"./polyfill"
    },
    shim: {
    　　'gisMapConstant':['jquery'],
        'mapConstants':['jquery'],
    　　'vortexBMap': ['jquery','event','MapUtil','gisMapConstant','mapConstants', 'BaiDuMap'],
        'DistanceTool':['BaiDuMap'],
        'InfoBox':['BaiDuMap'],
        'TextIconOverlay':['BaiDuMap'],
        'MarkerClusterer':['BaiDuMap'],
        'DrawingManager':['BaiDuMap'],
        'GeoUtils':['BaiDuMap'],
        'Heatmap':['BaiDuMap'],
        'TrafficControl':['BaiDuMap'],
        'app':["mConstant","downloadJS","jqueryForm","md5","sha","polyfill"]
    }
});

require(['app'], function () {
    
});