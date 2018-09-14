import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import { Input, Icon, Radio,Select ,TreeSelect,Collapse} from 'antd';
import styles from './item.less';
import VortexUpload from '../../vtxCommon/VortexUpload/VortexUpload';

const Panel = Collapse.Panel;
const ViewItem = (props)=>{
	const {contentProps ,modalProps} = props;
	const {orgName,orgId,name,code,gender,birthday,credentialNum,nationId,nationName,maritalStatusId,maritalStatusName,politicalStatusId,politicalStatusName,joinWorkTime,
			workYearLimit,isLeave,leaveTime,workTypeCode,orderIndex,description,birthPlace,presentPlace,livePlace,phone,officeTel,workTypeName,
			email,innerEmail,graduate,educationId,educationName,authorizeId,authorizeName,postId,postName,partyPostId,partyPostName,entryHereTime,
			idCard,socialSecurityNo,socialSecuritycase,outSourcingComp,outSourcing,companyName,isWillMan,address,willCheckDivisionNames,willWorkUnit,
			educationList,nationList,marriageListList,politicalStatusList,workTypeList,authorizeList,postList,socialSecurityList,partyPostList,photograph,fileListVersion,fileList} = contentProps;
    
    const turnIsLeave = (data)=>{
        if(data == '1'){
            return '离职'
        }else if(data == '2'){
            return '退休';
        }else {
            return '在职';
        }
    }
    const uploadProps = {
        fileList:fileList,
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        viewMode: true
    }
    return (
        <VtxModal {...modalProps}>
    	 	<div className={styles.formWrapper}>
                <Collapse bordered={false} defaultActiveKey={['1']}>
                <Panel header="基本信息" key="1">
    	 		<div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            所属机构：
                        </div>
                      	{orgId?orgName:companyName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            编码：
                        </div>
                      	{code}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            姓名：
                        </div>
                        {name}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            性别：
                        </div>
                      	{gender}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            生日：
                        </div>
                        {birthday}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            身份证：
                        </div>
                      	{credentialNum}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            民族：
                        </div>
                        {nationName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            婚姻：
                        </div>
                      	{maritalStatusName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            政治面貌：
                        </div>
                        {politicalStatusName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            参加工作时间：
                        </div>
                      	{joinWorkTime}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            工作年限：
                        </div>
                        {workYearLimit}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            在职状态：
                        </div>
                      	{turnIsLeave(isLeave)}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            离职退休日期：
                        </div>
                        {leaveTime}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            用工类型：
                        </div>
                      	{workTypeName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        {orderIndex}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>      
                        <div className={styles.label}>
                            描述：
                        </div>
                        {description}
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
                      	 {birthPlace}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            现籍：
                        </div>
                        {presentPlace}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            居住地：
                        </div>
                      	 {livePlace}
                    </div>
                </div>
                </Panel>
                <Panel header="联系方式" key="3">
                 <div className={styles.formRow}>
                 	 <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            手机号：
                        </div>
                        {phone}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            办公室电话：
                        </div>
                      	 {officeTel}
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            邮箱：
                        </div>
                        {email}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            内部邮件：
                        </div>
                      	 {innerEmail}
                    </div>
                </div>
                </Panel>
                <Panel header="教育背景" key="4">
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            毕业学校：
                        </div>
                        {graduate}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            学历：
                        </div>
                      	{educationName}
                    </div>
                </div>
                </Panel>
                <Panel header="用工情况" key="5">
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            人员编制性质：
                        </div>
                       {authorizeName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            进入本单位时间：
                        </div>
                      	{entryHereTime}
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            职位：
                        </div>
                       {postName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            职务：
                        </div>
                      	{partyPostName}
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            社保号：
                        </div>
                        {socialSecurityNo}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            社保缴纳情况：
                        </div>
                      	{
                            socialSecuritycase==1?
                            ['正常']:['停缴']
                        }
                    </div>
                </div>
                <div className={styles.formRow}>
                	<div className={styles.halfLayout}>
                        <div className={styles.label}>
                            是否外包：
                        </div>
                        {
                            outSourcing==false?
                            ['否']:['是']
                        }
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            外包公司：
                        </div>
                      	{outSourcingComp}
                    </div>
                </div>
                 <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            ID卡号：
                        </div>
                      	{idCard}
                    </div>
                </div>
                </Panel>
                <Panel header="意愿情况" key="6">
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            是否志愿者：
                        </div>
                      	{isWillMan==='true'?'是':'否'}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            工作单位：
                        </div>
                        {willWorkUnit}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            意愿检查区域：
                        </div>
                        {willCheckDivisionNames}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            地址：
                        </div>
                        {address}
                    </div>
                </div>
                </Panel>
                </Collapse>
    	 	</div>
        </VtxModal>
    	)
}
export default ViewItem;