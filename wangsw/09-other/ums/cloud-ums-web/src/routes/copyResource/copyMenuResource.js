import React from 'react';
import { connect } from 'dva';
import styles from './copyMenuResource.less';
import {Button,Select,message,Modal} from 'antd';
import SourceMenuTree from '../../components/copyResource/sourceMenuTree';
import TargetMenuTree from '../../components/copyResource/targetMenuTree';
import VtxTreeSelect from '../../components/vtxCommon/VtxTreeSelect/VtxTreeSelect';
const Option = Select.Option;

function CopyMenuResource({dispatch,copyMenuResource}){
    const {sourceUrl,sourceSystem,sourceSystemList,sourceTenant,sourceTenantList,targetUrl,targetSystem,targetSystemList,targetTenant,
        targetTenantList,sourceMenuTreeData,targetMenuTreeData,checkedKeys,selectedRoleKeys,saving}=copyMenuResource;
    
    const sourceMenuTreeProps = {
        sourceMenuTreeData,
        dispatch,
        checkedKeys,
        selectedRoleKeys
    }    
    const targetMenuTreeProps = {
        targetMenuTreeData,
        dispatch
    } 
    
    const startToCopy = ()=>{
        if(selectedRoleKeys.length == 0){
            message.error("请勾选需要拷贝的菜单");
        }else {
            Modal.confirm({
                title: '是否确认将资源库中选中的资源拷贝到目标库中？',
                okText: '确定',
                onOk: ()=>{
                    Modal.confirm({
                        title: '是否确定已经备份了目标库？',
                        okText: '确定',
                        onOk: ()=>{
                            dispatch({type:'copyMenuResource/startToCopy'});
                        }
                    });
                }
            });
        }
    }

	return (
		<div className={styles.main}>
            <div className={styles.content}>
                <div className={styles.leftPart}>
                    <div className={styles.head}>
                        <span className={styles.h1}>&nbsp;&nbsp;&nbsp;资源库</span>
                        <span className={styles.url}>{sourceUrl}</span>
                    </div>
                    <div className={styles.select}>
                        租户：
                        {/* <Select value={sourceTenant} style={{ width: 230,'margin-right':'10px' }} onSelect={(value,option)=>{
                            dispatch({type:'copyMenuResource/updateState',payload:{
                                sourceTenant:value,
                                sourceSystem: '',
                                sourceMenuTreeData: [],
                                selectedRoleKeys: [],
                                checkedKeys: []
                            }});
                            dispatch({type:'copyMenuResource/getSourceSystemList'});    
                        }}>
                            {sourceTenantList.map((item)=>{return <Option value={item.id}>{item.tenantName}</Option>})}
                        </Select> */}
                        <VtxTreeSelect
                            data={sourceTenantList}
                            value={[sourceTenant]}
                            showSearch={true}
                            style={{ width: 230,'marginRight':'10px' }} 
                            onChange={({allValue,allLabel,value,label})=>{
                                dispatch({type:'copyMenuResource/updateState',payload:{
                                    sourceTenant:value[0],
                                    sourceSystem: '',
                                    sourceMenuTreeData: [],
                                    selectedRoleKeys: [],
                                    checkedKeys: []
                                }});
                                dispatch({type:'copyMenuResource/getSourceSystemList'});
                            }}
                        />
                        业务系统：
                        {/* <Select value={sourceSystem} style={{ width: 230 }} onSelect={(value,option)=>{
                            dispatch({type:'copyMenuResource/updateState',payload:{
                                sourceSystem:value,
                                sourceMenuTreeData: [],
                                selectedRoleKeys: [],
                                checkedKeys: []
                            }});
                            dispatch({type:'copyMenuResource/getSourceMenuTree'});    
                        }}>
                            {sourceSystemList.map((item)=>{return <Option value={item.id}>{item.systemName}</Option>})}
                        </Select> */}
                        <VtxTreeSelect
                            data={sourceSystemList}
                            value={[sourceSystem]}
                            showSearch={true}
                            style={{ width: 230 }}
                            onChange={({allValue,allLabel,value,label})=>{
                                dispatch({type:'copyMenuResource/updateState',payload:{
                                    sourceSystem:value[0],
                                    sourceMenuTreeData: [],
                                    selectedRoleKeys: [],
                                    checkedKeys: []
                                }});
                                dispatch({type:'copyMenuResource/getSourceMenuTree'});
                            }}
                        />
                    </div>
                    <div className={styles.tree}>
                        <SourceMenuTree {...sourceMenuTreeProps}/>
                    </div>
                </div>

                <div className={styles.rightPart}>
                    <div className={styles.head}>
                        <span className={styles.h1}>&nbsp;&nbsp;&nbsp;目标库</span>
                        <span className={styles.url}>{targetUrl}</span>
                    </div>
                    <div className={styles.select}>
                        租户：
                        {/* <Select value={targetTenant} style={{ width: 230,'margin-right':'10px' }} onSelect={(value,option)=>{
                            dispatch({type:'copyMenuResource/updateState',payload:{
                                targetTenant:value,
                                targetMenuTreeData: [],
                                targetSystem: ''
                            }});
                            dispatch({type:'copyMenuResource/getTargetSystemList'});    
                        }}>
                            {targetTenantList.map((item)=>{return <Option value={item.id}>{item.tenantName}</Option>})}
                        </Select> */}
                        <VtxTreeSelect
                            data={targetTenantList}
                            value={[targetTenant]}
                            showSearch={true}
                            style={{ width: 230,'marginRight':'10px' }}
                            onChange={({allValue,allLabel,value,label})=>{
                                dispatch({type:'copyMenuResource/updateState',payload:{
                                    targetTenant:value[0],
                                    targetMenuTreeData: [],
                                    targetSystem: ''
                                }});
                                dispatch({type:'copyMenuResource/getTargetSystemList'}); 
                            }}
                        />
                        业务系统：
                        {/* <Select value={targetSystem} style={{ width: 230 }} onSelect={(value,option)=>{
                            dispatch({type:'copyMenuResource/updateState',payload:{
                                targetSystem:value,
                                targetMenuTreeData: []
                            }});
                            dispatch({type:'copyMenuResource/getTargetMenuTree'});    
                        }}>
                            {targetSystemList.map((item)=>{return <Option value={item.id}>{item.systemName}</Option>})}
                        </Select> */}
                        <VtxTreeSelect
                            data={targetSystemList}
                            value={[targetSystem]}
                            showSearch={true}
                            style={{ width: 230 }}
                            onChange={({allValue,allLabel,value,label})=>{
                                dispatch({type:'copyMenuResource/updateState',payload:{
                                    targetSystem:value[0],
                                    targetMenuTreeData: []
                                }});
                                dispatch({type:'copyMenuResource/getTargetMenuTree'}); 
                            }}
                        />
                    </div>
                    <div className={styles.tree}>
                        <TargetMenuTree {...targetMenuTreeProps}/>
                    </div>
                </div>
            </div>
            <div className={styles.foot}>
                <Button loading={saving} onClick={startToCopy}>开始拷贝</Button>
                <span className={styles.warning}>（注：拷贝之前，一定需要备份目标库，否则可能会导致数据错乱）</span>
            </div>
		</div>
    )
}
export default connect(({copyMenuResource})=>({copyMenuResource}))(CopyMenuResource);
