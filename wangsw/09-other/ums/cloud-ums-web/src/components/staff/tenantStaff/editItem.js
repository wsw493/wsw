import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import StateSelect from '../../vtxCommon/VtxForm/stateSelect';
import VtxDatePicker from '../../vtxCommon/VtxDate/VtxDatePicker';
import { Input, Icon, Radio,Select ,TreeSelect, Collapse, message} from 'antd';
import styles from './item.less';
import {formValidation} from '../../../utils/toolFunctions';
const Option = Select.Option;
const RadioGroup = Radio.Group;
const Panel = Collapse.Panel;
import {validate} from '../../regExpression';
import VtxTreeSelect from '../../vtxCommon/VtxTreeSelect/VtxTreeSelect';
import VortexUpload from '../../vtxCommon/VortexUpload/VortexUpload';

const EditItem = (props)=>{
	const {modalProps,contentProps } = props;
	const {id,visible,checkState,orgName,orgId,name,code,gender,birthday,credentialNum,nationId,maritalStatusId,politicalStatusId,joinWorkTime,
			workYearLimit,isLeave,leaveTime,workTypeCode,orderIndex,description,birthPlace,presentPlace,livePlace,phone,officeTel,
			email,innerEmail,graduate,educationId,educationName,authorizeId,authorizeName,postId,postName,partyPostId,partyPostName,entryHereTime,
            idCard,socialSecurityNo,socialSecuritycase,outSourcingComp,outSourcing,baseData,selectOrgTree,departmentId,codeUnique,
            phoneUnique,checkOrder,checkYear,isWillMan,address,divisionTree,willCheckDivisionIds,willWorkUnit,photograph,fileListVersion,fileList} = contentProps;
    const {updateItem,validateStaff,validateStaffPhone} = contentProps;
    function disabledDate(current) {
      return current && current.valueOf() > Date.now();
    }
    const uploadProps = {
        fileList:fileList,
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        onSuccess(file){
            message.info(`${file.name} 上传成功`);
            var fileList = [{id:file.id,name:file.name}];
            updateItem({photograph:JSON.stringify(fileList),fileList});
        },
        onError(res){
            message.info(`${res.name} 上传失败.`);
        },
        onRemove(file){
            updateItem({fileList:[],photograph:"[]"});
        }
    }
    function selectOrgTreeF(data,tid){
        if(typeof(data) == 'underfined'){
            data = [];
        }
        data.map((item)=>{
            if(item.key == tid){
                if(item.nodeType == 'org'){
                    updateItem({
                        orgId: item.key,
                        orgName: item.attr.attributes.name,
                        departmentId: item.attr.attributes.departmentId,
                        tenantId:item.attr.attributes.tenantId
                    });
                }else {
                    updateItem({
                        orgId: '',
                        orgName: item.attr.attributes.name,
                        departmentId: item.key,
                        tenantId:item.attr.attributes.tenantId
                    });
                }
            }
            if(item.children.length > 0){
                selectOrgTreeF(item.children,tid);
            }
        });
    }
    return (
    	 <VtxModal {...modalProps}>
    	 	<div className={styles.formWrapper}>
                <Collapse bordered={false} defaultActiveKey={['1']}>
                <Panel header="基本信息" key="1">
    	 		<div className={styles.formRow} style={{height:'46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            所属机构：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={selectOrgTree}
                                value={orgId!=''?[orgId]:[departmentId]}
                                treeDefaultExpandedKeys={['-1']}
                                onChange={({allValue,allLabel,value,label})=>{
                                    selectOrgTreeF(selectOrgTree,value[0]);
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                      	<StateInput onChange={(e)=>{
                            var code = e.target.value;
                            updateItem({code});
                            validateStaff({
                                id,
                                key: 'code',
                                code
                            });
                        }} value={code} errorMsg={codeUnique?'编码重复':validate(['required','codeValidator'],code)} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            姓名：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({name: e.target.value}); 
                        }} value={name} errorMsg={validate(['required','nameValidator'],name)} 
                        validated={checkState?(validate(['required','nameValidator'],name)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            性别：
                        </div>
                      	<RadioGroup onChange={(e)=>{
                            updateItem({gender: e.target.value}); 
                        }} value={gender} className={styles.radio}>
                            <Radio value='男'>男</Radio>
                            <Radio value='女'>女</Radio>
                         </RadioGroup>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            生日：
                        </div>
                        <VtxDatePicker value={birthday} style={{width:"300px"}} disabledDate={disabledDate}
	                        showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
	                       		updateItem({birthday: dateString}); 
	                       }}
	                    /> 
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            身份证：
                        </div>
                      	<StateInput onChange={(e)=>{
                            updateItem({credentialNum: e.target.value}); 
                        }} value={credentialNum}  errorMsg={validate(['empty','idCard'],credentialNum)}
                        validated={checkState?(validate(['empty','idCard'],credentialNum)=='') :true}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            民族：
                        </div>
                        <StateSelect onChange={(value,label)=>{
                            updateItem({'nationId': value}); 
                        }} value={nationId}>
                        {
                            baseData.STAFF_NATION.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            婚姻：
                        </div>
                      	<StateSelect onChange={(value,label)=>{
                            updateItem({'maritalStatusId': value}); 
                        }} value={maritalStatusId}>
                        {
                            baseData.STAFF_MARITAL_STATUS.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            政治面貌：
                        </div>
                        <StateSelect onChange={(value,label)=>{
                            updateItem({'politicalStatusId': value}); 
                        }} value={politicalStatusId}>
                        {
                            baseData.STAFF_POLITICAL_STATUS.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            参加工作时间：
                        </div>
                      	<VtxDatePicker value={joinWorkTime} style={{width:"300px"}} disabledDate={disabledDate}
	                        showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
	                       		updateItem({joinWorkTime: dateString}); 
	                       }}
	                    /> 
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            工作年限：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({workYearLimit: e.target.value,checkYear:true}); 
                        }} value={workYearLimit} errorMsg={validate(['empty','number'],workYearLimit)}
                        validated={(id?checkYear:checkState)?(validate(['empty','number'],workYearLimit)=='') :true}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            在职状态：
                        </div>
                      	{/* <RadioGroup onChange={(e)=>{
                            updateItem({isLeave: e.target.value}); 
                        }} value={isLeave} className={styles.radio}>
                            <Radio value='是'>是</Radio>
                            <Radio value='否'>否</Radio>
                         </RadioGroup> */}
                         <StateSelect onChange={(value,label)=>{
                            updateItem({
                                'isLeave': value,
                                leaveTime: value!=2?'':leaveTime
                            }); 
                            }} value={isLeave}>
                            <Option value={'0'}>在职</Option>
                            <Option value={'1'}>离职</Option>
                            <Option value={'2'}>退休</Option>
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            离职退休日期：
                        </div>
                        <VtxDatePicker value={leaveTime} style={{width:"300px"}} disabledDate={disabledDate} disabled={isLeave!='2'}
	                        showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
	                       		updateItem({leaveTime: dateString}); 
	                       }}
	                    /> 
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            用工类型：
                        </div>
                      	<StateSelect onChange={(value,label)=>{
                            updateItem({'workTypeCode': value}); 
                        }} value={workTypeCode}>
                        {
                            baseData.STAFF_WORK_TYPE.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value,checkOrder:true}); 
                        }} value={orderIndex} errorMsg={validate(['number'],orderIndex)}
                        validated={(id?checkOrder:checkState)?(validate(['empty','number'],orderIndex)=='') :true}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>      
                        <div className={styles.label}>
                            描述：
                        </div>
                        <StateInput type="textarea" onChange={(e)=>{
                            updateItem({description: e.target.value}); 
                        }} value={description}/>
                    </div>
                    <div className={styles.halfLayout}>      
                        <div className={styles.label}>
                            照片：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VortexUpload {...uploadProps}/>
                        </div>
                    </div>
                </div>
                </Panel>
                <Panel header="居住情况" key="2">
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            原籍：
                        </div>
                      	 <StateInput onChange={(e)=>{
                            updateItem({birthPlace: e.target.value}); 
                        }} value={birthPlace}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            现籍：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({presentPlace: e.target.value}); 
                        }} value={presentPlace}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            居住地：
                        </div>
                      	 <StateInput onChange={(e)=>{
                            updateItem({livePlace: e.target.value}); 
                        }} value={livePlace}/>
                    </div>
                </div>
                </Panel>
                <Panel header="联系方式" key="3">
                 <div className={styles.formRow}>
                 	 <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            手机号：
                        </div>
                        <StateInput onChange={(e)=>{
                            var phone = e.target.value;
                            updateItem({phone:e.target.value}); 
                            validateStaffPhone({
                                id,
                                key: 'phone',
                                phone
                            });
                        }} value={phone}  errorMsg={phoneUnique?"号码已使用":validate(['phone'],phone)}
                        validated={phoneUnique?false:(checkState?(validate(['phone'],phone)==''):true)}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            办公室电话：
                        </div>
                      	 <StateInput onChange={(e)=>{
                            updateItem({officeTel: e.target.value}); 
                        }} value={officeTel} errorMsg={validate(['empty','officePhone'],officeTel)}
                        validated={checkState?(validate(['empty','officePhone'],officeTel)==''):true}/>
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            邮箱：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({email: e.target.value}); 
                        }} value={email} errorMsg={validate(['empty','email'],email)}
                        validated={checkState?(validate(['empty','email'],email)==''):true}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            内部邮件：
                        </div>
                      	 <StateInput onChange={(e)=>{
                            updateItem({innerEmail: e.target.value}); 
                        }} value={innerEmail} errorMsg={validate(['empty','email'],innerEmail)}
                        validated={checkState?(validate(['empty','email'],innerEmail)==''):true}/>
                    </div>
                </div>
                </Panel>
                <Panel header="教育背景" key="4">
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            毕业学校：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({graduate: e.target.value}); 
                        }} value={graduate}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            学历：
                        </div>
                      	<StateSelect onChange={(value,label)=>{
                            updateItem({'educationId': value}); 
                        }} value={educationId}>
                        <Option value=''>无</Option>
                        {
                            baseData.STAFF_EDUCATION.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                </div>
                </Panel>
                <Panel header="用工情况" key="5">
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            人员编制性质：
                        </div>
                       <StateSelect onChange={(value,label)=>{
                            updateItem({'authorizeId': value}); 
                        }} value={authorizeId}>
                        <Option value=''>无</Option>
                        {
                            baseData.STAFF_AUTHORIZE.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            进入本单位时间：
                        </div>
                      	<VtxDatePicker value={entryHereTime} style={{width:"300px"}} disabledDate={disabledDate}
	                       showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
	                       		updateItem({entryHereTime: dateString}); 
	                       }}
	                    /> 
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            职位：
                        </div>
                       <StateSelect onChange={(value,label)=>{
                            updateItem({'postId': value}); 
                        }} value={postId}>
                        <Option value=''>其他</Option>
                        {
                            baseData.STAFF_POSITION.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            职务：
                        </div>
                      	<StateSelect onChange={(value,label)=>{
                            updateItem({'partyPostId': value}); 
                        }} value={partyPostId}>
                        {
                            baseData.STAFF_POST.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            社保号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({socialSecurityNo: e.target.value}); 
                        }} value={socialSecurityNo}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            社保缴纳情况：
                        </div>
                      	<StateSelect onChange={(value,label)=>{
                            updateItem({'socialSecuritycase': value}); 
                        }} value={socialSecuritycase}>
                        <Option value=''>无</Option>
                        {
                            baseData.STAFF_SOCIAL_SECURITY_CASE.map((ele,index)=>{
                                return <Option value={ele.value} key={index}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            是否外包：
                        </div>
                        <RadioGroup onChange={(e)=>{
                            updateItem({outSourcing: e.target.value}); 
                            if(!e.target.value){
                                updateItem({outSourcingComp: ''}); 
                            }
                        }} value={outSourcing} className={styles.radio}>
                            <Radio value={true}>是</Radio>
                            <Radio value={false}>否</Radio>
                        </RadioGroup>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            外包公司：
                        </div>
                      	<StateInput onChange={(e)=>{
                            updateItem({outSourcingComp: e.target.value}); 
                        }} value={outSourcingComp} disabled={!outSourcing}/>
                    </div>
                </div>
                 <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            ID卡号：
                        </div>
                      	<StateInput onChange={(e)=>{
                            updateItem({idCard: e.target.value}); 
                        }} value={idCard}/>
                    </div>
                </div>
                </Panel>
                <Panel header="意愿情况" key="6">
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            是否志愿者：
                        </div>
                      	<RadioGroup value={isWillMan} onChange={(e)=>{
                            updateItem({isWillMan:e.target.value});
                            if(e.target.value === 'false'){
                                updateItem({
                                    willWorkUnit:'',
                                    willCheckDivisionIds:'',
                                    address:''
                                });
                            }      
                        }}>
                            <Radio value='true'>是</Radio>
                            <Radio value='false'>否</Radio>
                        </RadioGroup>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            工作单位：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({willWorkUnit: e.target.value}); 
                        }} value={willWorkUnit} disabled={isWillMan=='false'}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            意愿检查区域：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={divisionTree}
                                value={willCheckDivisionIds?willCheckDivisionIds.split(','):[]}
                                treeDefaultExpandAll={true}
                                //treeCheckable={true}
                                multiple={true}
                                disabled={isWillMan=='false'}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({willCheckDivisionIds:value.join()});
                                }}
                            />
                        </div>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            地址：
                        </div>
                        <StateInput type='textarea' onChange={(e)=>{
                            updateItem({address: e.target.value}); 
                        }} value={address} disabled={isWillMan=='false'}/>
                    </div>
                </div>
                </Panel>
                </Collapse>
    	 	</div>
    	 </VtxModal>
    	)
}
export default EditItem;