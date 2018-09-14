import React, {Component,PropTypes} from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';
import {splitOverride} from '../../utils/toolFunctions';

class OrgWorkElementTree extends React.Component {
    constructor(props){
        super(props);
        this.treeObj = {};
		this.state = {
            orgWorkElementTree: props.orgWorkElementTree,
            initWorkElementKeys: props.initWorkElementKeys,
            checkedKeys_elem: props.checkedKeys_elem
		}
    }
    
    render(){
        let tree = this.props;
        const dispatch = tree.dispatch;
        let orgWorkElementTree=tree.orgWorkElementTree,
            initWorkElementKeys=tree.initWorkElementKeys,
            checkedKeys_elem=tree.checkedKeys_elem,
            searchName=tree.searchName;

        let treeProps = {
            data: orgWorkElementTree,
            isExpandAll:'openAll',
            expandedKeys: checkedKeys_elem.length==0?['-1']:checkedKeys_elem,
            defaultExpandAll:true,
            checkable:true,
            checkedKeys: checkedKeys_elem,
            isShowSearchInput: true,
            searchInput:{
                color:'#f50',
                render: ()=>{
                    return <div></div>
                }
            },
            onCheck: ({key,isChecked,checkedKeys,treeNode,leafNode})=>{
                var initWorkElementKeys = [];
                var checkedWorkElementList=[];
                var mapPoints2=[];
                var mapLines2=[];
                var mapPolygons2=[];
                var mapCircles2=[];
                var mapCenter=[];
                var mapVisiblePoints = [];
                for(var i=0;i<leafNode.length;i++){
                    if (leafNode[i].nodeType == 'WorkElement') {
                        initWorkElementKeys.push(leafNode[i].key);
    
                        var shape = leafNode[i].attr.attributes.shape;
                        var lonLatStr = leafNode[i].attr.attributes.paramsDone;
                        mapVisiblePoints.push("workE"+i);
                        checkedWorkElementList.push({attr:leafNode[i].attr.attributes,mapId:"workE"+i});
                        switch(shape){
                            case "point":
                                var arr = lonLatStr.split(",");
                                mapPoints2.push({id:"workE"+i,longitude:arr[0],latitude:arr[1]});
                                mapCenter = [arr[0],arr[1]];
                                break;
                            case "line":
                            case "polyline":
                                mapLines2.push({id:"workE"+i,paths:splitOverride(lonLatStr)});
                                break;
                            case "polygon":
                                mapPolygons2.push({id:"workE"+i,rings:splitOverride(lonLatStr)});
                                break;
                            case "circle":
                                var arr = lonLatStr.split(",");
                                mapCircles2.push({id:"workE"+i,longitude:arr[0],latitude:arr[1],radius:leafNode[i].attr.attributes.radius});
                                mapCenter = [arr[0],arr[1]];
                                break;
                            case "rectangle":
                                mapPolygons2.push({id:"workE"+i,rings:splitOverride(lonLatStr)});
                                break;
                        }
                    }
                }
                dispatch({type:'workElementGis/updateState',payload:{setCenter:true,mapPanelPosition:{display:"none"}}});
                dispatch({type:"workElementGis/clearInitMap"});
                dispatch({type:"workElementGis/updateState",payload:{initWorkElementKeys,checkedKeys_elem:checkedKeys}});
                // dispatch({type:"workElementGis/initWorkElement"});
                dispatch({type:"workElementGis/updateState",payload:{
                    mapPoints2: mapPoints2,
                    mapLines2: mapLines2,
                    mapCircles2: mapCircles2,
                    mapPolygons2: mapPolygons2,
                    mapCenter:mapCenter,
                    checkedWorkElementList: checkedWorkElementList,
                    mapVisiblePoints: mapVisiblePoints
                  }});
                dispatch({type:'workElementGis/updateState',payload:{setCenter:false}});
            }
        };

        return (
            <VtxTree ref={(treeobj)=>{this.treeObj = treeobj;}} {...treeProps}/>
        );
    }
}

export default OrgWorkElementTree;