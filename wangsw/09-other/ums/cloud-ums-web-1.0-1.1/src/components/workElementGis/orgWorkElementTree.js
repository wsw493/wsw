import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const OrgWorkElementTree = ({dispatch,orgWorkElementTree,initWorkElementKeys,checkedKeys_elem,searchName}) => {
	if(typeof(orgWorkElementTree) == "undefined"){
		orgWorkElementTree = [];
    }
    const splitOverride = (data)=>{
        var arrObj = [];
        if (data != "") {
          var args1 = data.split(";");
          for(var i=0;i<args1.length;i++){
            var args2 = args1[i].split(",");
            arrObj.push([args2[0],args2[1]]);
          } 
        }
        return arrObj;
    }
	const treeProps = {
		data: orgWorkElementTree,
        isExpandAll:'other',
        expandedKeys: checkedKeys_elem==0?['-1']:checkedKeys_elem,
		defaultExpandAll:true,
		checkable:true,
        checkedKeys: checkedKeys_elem,
        // isShowSearchInput: true,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} value={searchName} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
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
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default OrgWorkElementTree;