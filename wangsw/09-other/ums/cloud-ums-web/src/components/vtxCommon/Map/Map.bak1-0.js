import React, {Component,PropTypes} from 'react';
import styles from './Map.less';

import {whetherIs} from '../../config/config';

class Map extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            mapId: props.mapId,
            center: props.mapCenter,
            maxZoom: props.maxZoom,
            minZoom: props.minZoom,
            gis: new VortexBMap(),
            pointIds: [],//点的所有id
            lineIds: [],//线的所有id
            polygonIds: [],//面的所有id
            circleIds: [],//圆的所有id
            editId: '',//当前编辑的图元id
            editGraphic: '',//当前编辑完后图元所有数据
            boundaryInfo: [],//当前画出的边界线的id和区域名
            defaultPoint: './resources/images/defaultMarker.png',//默认点
            drawIds: {//绘制工具id集合
                point: [],
                polyline: [],
                polygon: [],
                circle: [],
                rectangle: []
            }
        }
    }
    init(){
        let t =this;
        t.createMap();
        const {
            mapPoints,mapLines,mapPolygons,mapCircles,
            mapVisiblePoints,mapCluster,mapZoomLevel
        } = this.props;
        let {boundaryName} = this.props;
        let {boundaryInfo,pointIds,lineIds,polygonIds,circleIds} = this.state;
        //添加点
        //Math.max(2,3)
        //MapUtil.isArray
        if(whetherIs('array',mapPoints)){
            t.addPoint(mapPoints);
        }
        //添加线
        if(whetherIs('array',mapLines)){
            t.addLine(mapLines);
        }
        //添加面
        if(whetherIs('array',mapPolygons)){
            t.addPolygon(mapPolygons);
        }
        //添加圆
        if(whetherIs('array',mapCircles)){
            t.addCircle(mapCircles);
        }
        //初始展示的视野范围
        if(mapVisiblePoints){
            switch(mapVisiblePoints.fitView){
                case 'point':
                    t.setVisiblePoints(pointIds,mapVisiblePoints.type);
                break;
                case 'line':
                    t.setVisiblePoints(lineIds,mapVisiblePoints.type);
                break;
                case 'polygon':
                    t.setVisiblePoints(polygonIds,mapVisiblePoints.type);
                break;
                case 'circle':
                    t.setVisiblePoints(circleIds,mapVisiblePoints.type);
                break;
                case 'all':
                    t.setVisiblePoints(pointIds.concat(lineIds).concat(polygonIds).concat(circleIds),mapVisiblePoints.type);
                break;
                default:
                    t.setVisiblePoints(mapVisiblePoints,mapVisiblePoints.type);
                break;
            }
        }
        //设置中心点
        t.setCenter(t.state.center);
        //设置点聚合
        if(whetherIs('array',mapCluster)){
            t.cluster(mapCluster);
        }
        //设置比例尺
        if(mapZoomLevel){
            t.setZoomLevel(mapZoomLevel);
        }
        //画边界线
        if(whetherIs('array',boundaryName) && !!boundaryName[0]){
            let boundaryData = t.dataMatching(boundaryName,boundaryInfo,'id');
            boundaryData[1].map((item)=>{
                t.removeGraphic(item.lineId);
            });
            boundaryData[0].map((item)=>{
                t.getBoundary(item);
            })
        }
        //图元点击事件
        t.clickGraphic();
        //地图拖动事件
        t.dragMapStart();//拖动之前
        t.dragMapEnd();//拖动结束后
        t.moveStart();
        t.moveEnd();
        //每次图元编辑后
        t.editGraphicChange();
        //鼠标悬浮图元事件
        t.mouseOverGraphic();
        t.mouseOutGraphic();
        //地图缩放事件
        t.zoomStart();
        t.zoomEnd();
        //绘制结束回调事件
        t.drawEnd();
        //是否设置比例尺
        if(t.props.showControl){
            t.showControl();
        }
        //返回计算距离方法
        if(whetherIs('function',t.props.calculatePointsDistance)){
            t.props.calculatePointsDistance(t.calculatePointsDistance.bind(t));
        }
        //回调显示方法
        if(t.props.showGraphicById){
            t.props.showGraphicById(t.showGraphicById.bind(t));
        }
        //回调隐藏方法
        if(t.props.hideGraphicById){
            t.props.hideGraphicById(t.hideGraphicById.bind(t));
        }
    }
    //创建地图
    createMap () {
        let t =this;
        let options ={};
        if(!!t.state.maxZoom){
            options.maxZoom = t.state.maxZoom
        }
        if(!!t.state.minZoom){
            options.minZoom = t.state.minZoom
        }
        t.state.gis.createMap(t.state.mapId,options);
    }
    //清空地图所有图元
    clearAll (){
        let t = this;
        t.state.gis.clear();
        //清空历史数据记录
        t.setState({
            pointIds: [],
            lineIds: [],
            polygonIds: [],
            circleIds: [],
            boundaryInfo: [],
            editId: '',
            editGraphic: '',
            drawIds: {
                point: [],
                polyline: [],
                polygon: [],
                circle: [],
                rectangle: []
            }
        });
    }
    /*set方法*/
    //设置地图中心位置 lng/经度  lat/纬度
    setCenter (gt) {
        let t =this;
        if(gt){
            t.state.gis.setCenter(gt[0],gt[1]);
            t.setState({center:gt});
        }else{
            t.state.gis.setCenter(117.468021,39.890092);
            t.setState({center:[117.468021,39.890092]});
        }
    }
    //将制定图元展示在视野内 (强制改变地图中心位置)
    /*
        参数arg格式如下1,2
        1.string   格式如:'1,a,2,3,4'
        2.数组 ['1','2']
    */
    setVisiblePoints (arg,type) {
        let t = this;
        let ary = [];
        if(typeof(arg) === 'string'){
            ary = arg.split(',');
        }else if(typeof(arg) === 'object' && !!arg.length){
            ary = arg;
        }
        let obj = t.state.gis.getFitView(ary); 
        if(type == 'all'){
            t.state.gis.setFitview(ary);
        }else if(type == 'zoom'){
            t.setZoomLevel(obj.zoom);
        }else if(type == 'center'){
            t.setCenter([obj.center.lng,obj.center.lat]);
        }
    }
    //设置地图比例尺
    setZoomLevel (zoom) {
        let t =this;
        t.state.gis.setZoom(zoom);
    }
    /*get方法*/
    //获取当前地图的中心位置
    // 
    getCurrentCenter(){
        let t =this;
        return t.state.gis.getNowCenter();
    }
    //获取当前比例尺
    getZoomLevel (){
        let t =this;
        return t.state.gis.getZoom();
    }
    //获取当前地图边框左右边角经纬度,中心点位置,和比例尺,半径距离
    getMapExtent(){
        let t =this;
        let obj = t.state.gis.getMapExtent();
        let radius = t.calculatePointsDistance([obj.nowCenter.lng,obj.nowCenter.lat],[obj.northEast.lng,obj.northEast.lat]);
        obj.radius = radius;
        return obj;
    }
    //聚合地图图元(arg为空时聚合全部点)
    cluster (arg) {
        let t = this;
        let ary = [];
        if(!arg){
            let {pointIds} = t.state;
            ary = pointIds;
        }else{
            if(Object.prototype.toString.apply(arg) === '[object Array]')
                ary = arg;
        }
        t.state.gis.cluster(ary);
    }
    //设置比例尺
    showControl (){
        let t = this;
        t.state.gis.showControl(t.props.showControl.type,t.props.showControl.location);
    }
    //获取图元数据
    /*
        attributes 初始化数据(即添加点时的初始数据)
        geometryType:point/polyline/polygon/circle
        mapLayer 
            点 rc / 线 Ac / 面 zc / 圆 Bc
        geometry 格式化数据 
            点 {type:point,x:lng,y:lat}
            线 {type:polyline,paths:[[lng,lat],[lng,lat]]}
            面 {type:polygon,rings:[[lng,lat],[lng,lat],[lng,lat]]}
            圆 {type:circle,x:lng,y:lat,radius:xxx}
    */
    getGraphic (id) {
        let t = this;
        return t.state.gis.getGraphic(id);
    }
    //显示隐藏的图元
    showGraphicById (id){
        let t = this;
        t.state.gis.showGraphicById(id);
    }
    //隐藏图元
    hideGraphicById(id){
        let t = this;
        t.state.gis.hideGraphicById(id);
    }
    //画出对应边界线 name区域名
    getBoundary(name){
        let t = this;
        let id = 'boundary' + new Date().getTime();
        t.state.gis.getBoundary(name,(ary)=>{
            let path = ary.boundaries[0].split(';').map((item,index)=>{
                return item.split(',');
            })
            t.addLine([{
                id: id,
                paths: path
            }])
            //便于解析{id:name,lineId:id}
            t.state.boundaryInfo.push({id:name,lineId:id});
        });

    }
    //地图加点(支持多个和单个)
    /*
        参数 [{
            id : 唯一id,
            longitude : 经度,
            latitude : 纬度,
            infoWindow : 是否有弹出 默认true,
            url : 图标url,
            markerContent: 点样式,//跟url冲突,存在markerContent时,url不展示
            config : {
                width:图标宽 默认30,
                height:图标高 默认30,
                labelContent: 下标文字,
                labelPixelX: x轴下标偏移量,
                labelPixelY: y轴下标偏移量,
            }
        }]
    */
    addPoint (arg) {
        let t = this;
        let pointIds = t.state.pointIds;
        arg.map(function (item,index) {
            if(pointIds.indexOf(item.id) === -1){
                let config = {
                    labelContent: '',
                    labelPixelX: -36,
                    labelPixelY: 34,
                    width : 30,
                    height : 30,
                };
                config = {...config,...item.config};
                t.state.gis.addPoint({
                    id : item.id,
                    longitude : item.longitude,
                    latitude : item.latitude,
                    infoWindow : item.infoWindow || true,
                    url : item.url || '',
                    markerContent: item.markerContent || '',
                    canShowLabel: item.canShowLabel || false,
                    config: config,
                    other:item
                });
                pointIds.push(item.id);
            }
        });
        t.setState({pointIds});
    }
    //更新地图点数据(支持多个和单个) {暂时有不用,未完善}
    /*
        参数 [{
                id : 唯一id,
                longitude : 经度,
                latitude : 纬度,
                infoWindow : 是否有弹出 默认true,
                url : 图标url,
                markerContent: 点样式,//跟url冲突,存在markerContent时,url不展示
                config : {
                    width:图标宽 默认30,
                    height:图标高 默认30,
                    labelContent: 下标文字,
                    labelPixelX: x轴下标偏移量,
                    labelPixelY: y轴下标偏移量,
                }
            }]
     */
    updatePoint (arg) {
        var t = this;
        arg.map(function (item,index) {
            let config = {
                labelContent: '',
                labelPixelX: -36,
                labelPixelY: 34,
                width : 30,
                height : 30,
            };
            t.state.gis.updatePoint({
                id : item.id,
                longitude : item.longitude,
                latitude : item.latitude,
                infoWindow : item.infoWindow || true,
                url : item.url || '',
                markerContent: item.markerContent || '',
                canShowLabel: item.canShowLabel || false,
                config : {...config,...item.config},
                other:item
            });
        })
    }
    //地图加线(支持多个和单个) paths:必须含2个点,即含有2个有经纬度的数组
    /*
        参数 [{
                id : 唯一id,
                paths:[[经度,纬度],[经度,纬度],[经度,纬度]],
                infoWindow : 是否有弹出 默认true,
                config : {
                    lineType : 线类型（实线solid，虚线dashed）默认实线solid
                    lineWidth :  线宽, 默认5
                    color : 线颜色, 默认[]
                    pellucidity : 线透明度(0-1), 默认1
                }
            }]
     */
    addLine (arg) {
        var t = this;
        let lineIds = t.state.lineIds;
        arg.map(function (item,index) {
            if(lineIds.indexOf(item.id) === -1){
                let config = {
                    lineType : 'solid',
                    lineWidth : 5,
                    color : '',
                    pellucidity : 1
                };
                t.state.gis.addLine({
                    id : item.id,
                    paths : [item.paths],
                    infoWindow : item.infoWindow || true,
                    config : {...config,...item.config},
                    other:item
                });
                lineIds.push(item.id);
            }
        });
        t.setState({lineIds});
    }
    //地图加面(支持多个和单个) rings:必须含3个点,即含有3个有经纬度的数组
    /*
        参数 [{
                id : 唯一id,
                rings:[[经度,纬度],[经度,纬度],[经度,纬度]],
                infoWindow : 是否有弹出 默认true,
                config : {
                    lineType : 线类型（实线solid，虚线dashed）默认实线solid
                    lineWidth :  线宽, 默认5
                    color : 填充颜色, 默认[]
                    pellucidity : 填充透明度(0-1), 默认1
                    lineColor: 线颜色, 默认''
                    lineOpacity: 线透明度, 默认1
                }
            }]
     */
    addPolygon (arg) {
        var t = this;
        let polygonIds = t.state.polygonIds;
        arg.map(function (item,index) {
            if(polygonIds.indexOf(item.id) === -1){
                let config = {
                    lineType : 'solid',
                    lineWidth : 5,
                    color : "#fff",
                    pellucidity : .5,
                    lineColor : "",
                    lineOpacity : 1
                };
                t.state.gis.addPolygon({
                    id : item.id,
                    rings : [item.rings],
                    infoWindow : item.infoWindow || true,
                    config : {...config,...item.config},
                    other:item
                });
                polygonIds.push(item.id);
            }
        });
        t.setState({polygonIds});
    }
    //地图加面(支持多个和单个) rings:必须含3个点,即含有3个有经纬度的数组
    /*
        参数 [{
                id : 唯一id,
                longitude : 经度,
                latitude : 纬度,
                infoWindow : 是否有弹出 默认true,
                radius : 半径, 单位是m/米 默认50
                config : {
                    lineType : 线类型（实线solid，虚线dashed）默认实线solid
                    lineWidth :  线宽, 默认5
                    color : 填充颜色, 默认'#FFF'
                    pellucidity : 填充透明度(0-1), 默认1
                    lineColor: 线颜色, 默认''
                    lineOpacity: 线透明度, 默认1
                }
            }]
     */
    addCircle (arg) {
        var t = this;
        let circleIds = t.state.circleIds;
        arg.map(function (item,index) {
            if(circleIds.indexOf(item.id) === -1){
                let config = {
                    lineType : 'solid',
                    lineWidth : 5,
                    color : "#FFF",
                    pellucidity : .5,
                    lineColor : "rgb(58, 107, 219)",
                    lineOpacity : 1
                }
                t.state.gis.addCircle({
                    id : item.id,
                    longitude : item.longitude,
                    latitude : item.latitude,
                    radius : item.radius || 50,
                    infoWindow : item.infoWindow || true,
                    config : {...config,...item.config},
                    other:item
                });
                circleIds.push(item.id);
            }
        });
        t.setState({circleIds});
    }
    /*根据图元id,使图元变成可编辑状态*/
    doEdit (id){
        let t = this;
        let graphic = t.getGraphic(id);
        if(!graphic)
            return false;
        t.state.gis.doEdit(graphic);
        t.setState({editId:id})
    }
    //关闭编辑
    endEdit (){
        let t = this;
        let graphic = t.getGraphic(t.state.editId);
        if(!graphic)
            return false;
        t.state.gis.endEdit(graphic);
        //避免死循环
        if(t.state.editGraphic){
            t.props.editGraphicChange(t.state.editGraphic);
            t.setState({editGraphic:''});
        }
    }
    //编辑变动后
    editGraphicChange(){
        let t = this;
        if(typeof(t.props.editGraphicChange) === 'function'){
            t.state.gis.bind('graphicVortexChange', function(param,e) {
                let obj = {
                    param,e,
                    id: param.attributes.id,
                    geometry: param.geometry
                }
                t.props.editGraphicChange(obj);
                t.setState({editGraphic:obj});
            });
        }
    }
    //图元鼠标悬浮事件
    mouseOverGraphic(){
        let t = this;
        t.state.gis.bind('mouseOverGraphic', function(param,e) {
            let obj = {
                param,e,
                id: param.attributes.id,
            }
            // t.isSetTop(param.attributes.id,true);
            if(whetherIs('function',t.props.mouseOverGraphic)){
                t.props.mouseOverGraphic(obj);
            }
        });
    }
    //图元鼠标移开事件
    mouseOutGraphic(){
        let t = this;
        t.state.gis.bind('mouseOutGraphic', function(param,e) {
            let obj = {
                param,e,
                id: param.attributes.id,
            }
            //取消点的置顶效果
            console.log('out',param);
            // t.isSetTop(param.attributes.id,false);
            if(whetherIs('function',t.props.mouseOutGraphic)){
                t.props.mouseOutGraphic(obj);
            }
        });
    }
    //测距
    vtxRangingTool (backcall) {
        let t = this;
        t.state.gis.vtxRangingTool(function(obj){
            backcall(obj);
        });
    }
    //是否置顶图元
    isSetTop(id,type){
        let t = this;
        let graphic = t.getGraphic(id);
        t.state.gis.isSetTop(graphic,type);
    }
    //删除图元
    removeGraphic (id,type) {
        let t = this;
        var graphic = t.getGraphic(id);
        t.state.gis.removeGraphic(graphic);
        let ids;
        switch(type){
            case 'point':
                ids = t.state.pointIds;
            break;
            case 'line':
                ids = t.state.lineIds;
            break;
            case 'polygon':
                ids = t.state.polygonIds;
            break;
            case 'circle':
                ids = t.state.circleIds;
            break;
        }
        if(ids.indexOf(id) != -1){
            ids.splice(ids.indexOf(id),1);
        }
    }
    //点击图元事件
    clickGraphic () {
        let t = this;
        if(whetherIs('function',t.props.clickGraphic)){
            t.state.gis.bind('clickGraphic', function(param,e) {
                let obj = {
                    type: param.geometry.type,//图元类型
                    attributes: {...param.attributes.other,...{config:param.attributes.config}},//添加时图元信息
                    top: e.clientY,//当前点所在的位置(屏幕)
                    left: e.clientX,
                    e: e
                }
               t.props.clickGraphic(obj);
            });
        }
    }
    //拖拽地图开始
    dragMapStart(){
        let t = this;
        if(whetherIs('function',t.props.dragMapStart)){
            t.state.gis.bind('dragMapStart', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                //处理下数据,符合拖拽事件
               t.props.dragMapStart(obj);
            });
        }
    }
    //拖拽地图结束事件
    dragMapEnd() {
        let t = this;
        if(whetherIs('function',t.props.dragMapEnd)){
            t.state.gis.bind('dragMapEnd', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                //处理下数据,符合拖拽事件
               t.props.dragMapEnd(obj);
            });
        }
    }
    //地图移动开始事件
    moveStart(){
        let t = this;
        if(whetherIs('function',t.props.moveStart)){
            t.state.gis.bind('moveStart', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                //处理下数据,符合拖拽事件
                t.props.moveStart(obj);
            });
        }
    }
    //地图移动结束事件
    moveEnd(){
        let t = this;
        if(whetherIs('function',t.props.moveEnd)){
            t.state.gis.bind('moveEnd', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                //处理下数据,符合拖拽事件
                t.props.moveEnd(obj);
            });
        }
    }
    //地图更改缩放级别开始时触发触发此事件
    zoomStart(){
        let t =this;
        if(whetherIs('function',t.props.zoomStart)){
            t.state.gis.bind('zoomStart', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                t.props.zoomStart(obj);
            });
        }
    }
    //地图更改缩放级别结束时触发触发此事件
    zoomEnd(){
        let t =this;
        if(whetherIs('function',t.props.zoomEnd)){
            t.state.gis.bind('zoomEnd', function(param,e) {
                let obj = t.getMapExtent();
                obj.e = e;
                t.props.zoomEnd(obj);
            });
        }
    }
    //绘制图元
    /*
        参数
        geometryType:point/polyline/polygon/circle/rectangle  默认point
        parameter 样式 默认null 对象{}写入方式跟add方法一样(对应点线圆面)
        data //初始化数据   默认{id:'add'}
    */
    draw (obj) {
        var t = this;
        obj = obj || {};
        obj.data = obj.data || {};
        obj.data.id = obj.data.id || 'draw' + new Date().getTime();
        obj.geometryType = obj.geometryType || 'point';
        obj.parameter = obj.parameter || {url: t.state.defaultPoint};
        t.state.gis.draw(
            obj.geometryType,
            obj.parameter,
            obj.data
        );
        t.state.drawIds[obj.geometryType].push(obj.data.id);
    }
    //绘制图元结束回调
    drawEnd(){
        var t = this;
        if(whetherIs('function',t.props.drawEnd)){
            t.state.gis.bind('drawEnd', function(src) {
                t.props.drawEnd(src);
            });
        }
    }
    //计算2点间距离 单位m 精确到个位
    calculatePointsDistance(f,s){
        return Math.round(this.state.gis.calculatePointsDistance(f,s));
    }
    /*公共方法*/
    //判断对应参数是否是数组
    isArray (ary) {
        return Object.prototype.toString.call(ary) === '[object Array]';
    }
    //判断对应参数是否是数组
    isObject (obj) {
        return Object.prototype.toString.call(obj) === '[object Object]';
    }
    //判断对应参数是否是函数
    isFunction(param){
        return typeof(param) === 'function';
    }
    //数据解析(分析,新增,更新,删除对应的数据)
    dataMatching(oldData,newData,type){
        let updatePointId = [];
        oldData.map((item,index)=>{
            newData.map((itemM,indexM)=>{
                if(itemM[type] === item){
                    updatePointId.push(itemM);
                }
            })
        });
        updatePointId.map((item,index)=>{
            oldData = oldData.filter((itemI,indexI)=>{
                return item[type] !== itemI;
            });
            newData = newData.filter((itemI,indexI)=>{
                return item[type] !== itemI[type];
            });
        });
        return [oldData,newData,updatePointId];
    }
    render(){
        let t = this;
        let _map = this.props;
        return(
            <div id={_map.mapId} className={styles.map}></div>
        );
    }
    componentDidMount(){
        let t = this;
        t.init();
        // let z_index;
        // $(document).on('mouseover','label.BMapLabel',function(e){
        //     z_index = $(this).css('z-index');
        //     $(this).css('z-index',1);
        // })
        // $(document).on('mouseout','label.BMapLabel',function(e){
        //     $(this).css('z-index',z_index)
        // })
    }
    componentDidUpdate(prevProps, prevState) {//重新渲染结束
        let t = this;    
        //返回计算距离方法
        if(whetherIs('function',t.props.calculatePointsDistance)){
            t.props.calculatePointsDistance(t.calculatePointsDistance.bind(t));
        }
        //回调显示方法
        if(t.props.showGraphicById){
            t.props.showGraphicById(t.showGraphicById.bind(t));
        }
        //回调隐藏方法
        if(t.props.hideGraphicById){
            t.props.hideGraphicById(t.hideGraphicById.bind(t));
        }    
    }
    componentWillReceiveProps(nextProps,prevProps) {//已加载组件，收到新的参数时调用
        let t = this;
        //点/线旧数据
        let {pointIds,lineIds,polygonIds,circleIds,center,boundaryInfo} = t.state;
        //点/线新数据
        let {
            mapPoints,mapLines,mapPolygons,mapCircles,
            mapCenter,setCenter,
            mapVisiblePoints,setVisiblePoints,
            mapCluster,setCluster,
            mapZoomLevel,setZoomLevel,
            isRangingTool,mapRangingTool,
            editGraphicId,
            boundaryName,
            mapDraw,isDraw,
            isClearAll,
        } = nextProps;
        /*点数据处理
            pointData[2]相同的点,执行刷新
            pointData[1]的数据在idsForGraphicId中不存在的,执行新增
            pointData[0]数据中多余的id,执行删除
        */
        if(whetherIs('array',mapPoints)){
            //过滤编辑的图元
            let pointId = pointIds.filter((item)=>{return item !== editGraphicId});
            mapPoints = mapPoints.filter((item)=>{return item.id !== editGraphicId});
            let pointData = t.dataMatching(pointId,mapPoints,'id');
            //删在增之前,(因为增加后会刷新pointIds的值,造成多删的问题)
            pointData[0].map((item,index)=>{
                t.removeGraphic(item,'point');
            });
            // let len = pointIds.length;
            // for (let i = 0 ; i < len ; i++) {
            //     t.removeGraphic(pointIds[0],'point');
            // }
            //增加
            t.addPoint(pointData[1]);
            // t.addPoint(mapPoints);
            //更新
            t.updatePoint(pointData[2]);
        }
        /*
            线数据处理
            先全删除,再新增
        */
        if(whetherIs('array',mapLines)){
            //删在增之前,(因为增加后会刷新pointIds的值,造成多删的问题)
            //过滤编辑的图元
            let lineId = lineIds.filter((item)=>{return item !== editGraphicId});
            //过滤边界线
            lineId = t.dataMatching(lineId,boundaryInfo,'lineId')[0];
            mapLines = mapLines.filter((item)=>{return item.id !== editGraphicId});
            let len = lineId.length;
            for (let i = 0 ; i < len ; i++) {
                t.removeGraphic(lineId[i],'line');
            }
            //增加
            t.addLine(mapLines);
        }
        /*
            面数据处理
            先全删除,再新增
        */
        if(whetherIs('array',mapPolygons)){
            //删在增之前,(因为增加后会刷新pointIds的值,造成多删的问题)
            //过滤编辑的图元
            let polygonId = polygonIds.filter((item)=>{return item !== editGraphicId});
            mapPolygons = mapPolygons.filter((item)=>{return item.id !== editGraphicId});
            let len = polygonId.length;
            for (let i = 0 ; i < len ; i++) {
                t.removeGraphic(polygonId[i],'polygon');
            }
            //增加
            t.addPolygon(mapPolygons);
        }
        /*
            圆数据处理
            先全删除,再新增
        */
        if(whetherIs('array',mapCircles)){
            //删在增之前,(因为增加后会刷新pointIds的值,造成多删的问题)
            let circleId = circleIds.filter((item)=>{return item !== editGraphicId});
            mapCircles = mapCircles.filter((item)=>{return item.id !== editGraphicId});
            let len = circleId.length;
            for (let i = 0 ; i < len ; i++) {
                t.removeGraphic(circleId[i],'circle');
            }
            //增加
            t.addCircle(mapCircles);
        }
        //图元编辑调用
        if(editGraphicId){
            t.doEdit(editGraphicId);
        }else{
            t.endEdit();
        }
        //绘制边界线
        if(whetherIs('array',boundaryName) && !!boundaryName[0]){
            let boundaryData = t.dataMatching(boundaryName,boundaryInfo,'id');
            boundaryData[1].map((item)=>{
                t.removeGraphic(item.lineId,'line');
            });
            boundaryData[0].map((item)=>{
                t.getBoundary(item);
            });
        }
        //绘制图元
        if(isDraw){
            t.draw(mapDraw);
        }
        if(isClearAll){
            t.clearAll();
        }
        if(setVisiblePoints){
            switch(mapVisiblePoints.fitView){
                case 'point':
                    t.setVisiblePoints(pointIds,mapVisiblePoints.type);
                break;
                case 'line':
                    t.setVisiblePoints(lineIds,mapVisiblePoints.type);
                break;
                case 'polygon':
                    t.setVisiblePoints(polygonIds,mapVisiblePoints.type);
                break;
                case 'circle':
                    t.setVisiblePoints(circleIds,mapVisiblePoints.type);
                break;
                case 'all':
                    t.setVisiblePoints(pointIds.concat(lineIds).concat(polygonIds).concat(circleIds),mapVisiblePoints.type);
                break;
                default:
                    t.setVisiblePoints(mapVisiblePoints,mapVisiblePoints.type);
                break;
            }
        }
        //设置中心点
        if(setCenter){
            t.setCenter(mapCenter);
        }
        //设置点聚合
        if(setCluster){
            t.cluster(mapCluster);
        }
        //设置比例尺
        if(setZoomLevel){
            t.setZoomLevel(mapZoomLevel);
        }
        //测距工具调用
        if(isRangingTool && whetherIs('function',mapRangingTool)){
            t.vtxRangingTool(mapRangingTool);
        }
    }
}

export default Map;
