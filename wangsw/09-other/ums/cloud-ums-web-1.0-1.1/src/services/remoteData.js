import {request,requestFile,downloadFile} from '../utils/request';
import {getBaicPostData} from '../utils/toolFunctions';
// import {getBaicGetData} from '../utils/toolFunc';
//url
const sa = ManagementConstant.back_dynamic_suffix;
const read = ManagementConstant.permission_suffix_read;
const smvc = ManagementConstant.back_dynamic_suffix_smvc;

/********************角色组*********************/
//角色组列表数据
export async function getRoleGroupTable(param){
    return request('/cloud/management/rolegroup/pageList' + sa,{
        body: param
    })
}
//角色组树
export async function getRoleGroupTree(param){
    return request('/cloud/management/rolegroup/loadTree' + sa,{
        body: param
    })
}
//新增角色组
export async function saveRoleGroup(param){
    return request('/cloud/management/rolegroup/save' + sa,{
        body: param
    })
}
//角色组code,name唯一性校验
export async function validateRoleGroup(param){
    return request('/cloud/management/rolegroup/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改角色组
export async function updateRoleGroup(param){
    return request('/cloud/management/rolegroup/update' + sa,{
        body: param
    })
}
//删除角色组
export async function deleteRoleGroup(param){
    return request('/cloud/management/rolegroup/delete/'+ param + sa,{
        body: {}
    })
}
//删除角色组(批量)
export async function deletesRoleGroup(param){
    return request('/cloud/management/rolegroup/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
/********************角色*********************/
//角色列表数据
export async function getRoleTable(param){
    return request('/cloud/management/role/pageList' + sa,{
        body: param
    })
}

//新增角色
export async function saveRole(param){
    return request('/cloud/management/role/save' + sa,{
        body: param
    })
}
//角色code,name唯一性校验
export async function validateRole(param){
    return request('/cloud/management/role/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改角色
export async function updateRole(param){
    return request('/cloud/management/role/update' + sa,{
        body: param
    })
}

//删除角色(批量)
export async function deletesRole(param){
    return request('/cloud/management/role/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//角色详情by Id
export async function getRoleDtoById(param){
    return request('/cloud/management/role/loadById' + sa,{
        body: param
    })
}
//角色功能关系数据
export async function getRoleFunRelation(param){
    return request('/cloud/management/functionrole/dataList' + sa,{
        body: param
    })
}
//角色功能关系保存
export async function roleBindFunctionTreeSave(param){
    return request('/cloud/management/functionrole/save/' + param.id + sa,{
        body: param.param
    })
}
/********************功能组*********************/
//功能组列表数据
export async function getFunctionGroupTable(param){
    return request('/cloud/management/functiongroup/pageList' + sa,{
        body: param
    })
}
//功能组树
export async function getFunctionGroupTree(param){
    return request('/cloud/management/functiongroup/loadTree' + sa,{
        body: param
    })
}
//新增功能组
export async function saveFunctionGroup(param){
    return request('/cloud/management/functiongroup/save' + sa,{
        body: param
    })
}
//功能组code,name唯一性校验
export async function validateFunctionGroup(param){
    return request('/cloud/management/functiongroup/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改功能组
export async function updateFunctionGroup(param){
    return request('/cloud/management/functiongroup/update' + sa,{
        body: param
    })
}

//删除功能组
export async function deleteFunctionGroup(param){
    return request('/cloud/management/functiongroup/delete/'+ param + sa,{
        body: {}
    })
}
//删除功能组(批量)
export async function deletesFunctionGroup(param){
    return request('/cloud/management/functiongroup/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
/********************功能*********************/
//功能列表数据
export async function getFunctionsTable(param){
    return request('/cloud/management/function/pageList' + sa,{
        body: param
    })
}
//新增功能
export async function saveFunctions(param){
    return request('/cloud/management/function/save' + sa,{
        body: param
    })
}
//功能code,name唯一性校验
export async function validateFunctions(param){
    return request('/cloud/management/function/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改功能
export async function updateFunctions(param){
    return request('/cloud/management/function/update' + sa,{
        body: param
    })
}
//删除功能(批量)
export async function deletesFunctions(param){
    return request('/cloud/management/function/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//功能详情by Id
export async function getFunctionDtoById(param){
    return request('/cloud/management/function/loadById' + sa,{
        body: param
    })
}
//获取系统列表
export async function getSystemList(param){
    return request('/cloud/management/function/loadGoalSystem' + sa,{
        body: param
    })
}
//获取功能树
export async function getFunctionTree(param){
    return request('/cloud/management/function/loadTree' + sa,{
        body: param
    })
}
/********************租户*********************/
//租户列表数据
export async function getTenantTable(param){
    return request('/cloud/management/tenant/pageList' + sa,{
        body: param
    })
}
//行政区划树
export async function getDivisionTree(param){
    return request('/cloud/management/xzqh/loadTree' + sa,{
        body: param
    })
}
//新增租户
export async function saveTenant(param){
    return request('/cloud/management/tenant/add' + sa,{
        body: param
    })
}
//租户code,domain唯一性校验
export async function validateTenant(param){
    return request('/cloud/management/tenant/checkForm/'+ param.key + sa,{
        body: param.param
    })
}
//租户username唯一性校验(新增)
export async function validateTenantAccount(param){
    return request('/cloud/management/user/checkForAdd/userName' + sa,{
        body: param
    })
}
//租户username唯一性校验(修改)
export async function resetTenantUserPassword_U(param){
    return request('/cloud/management/user/checkForUpdate/userName' + sa,{
        body: param
    })
}
//修改租户
export async function updateTenant(param){
    return request('/cloud/management/tenant/update' + sa,{
        body: param
    })
}
//获取租户详细信息
export async function getTenantDtoById(param){
    return request('/cloud/management/tenant/loadTenantDtl' + sa,{
        body: param
    })
}
//禁用租户
export async function disableTenant(param){
    return request('/cloud/management/tenant/disable' + sa,{
        body: param
    })
}
//启用租户
export async function enableTenant(param){
    return request('/cloud/management/tenant/enable' + sa,{
        body: param
    })
}
//云系统列表数据
export async function getCloudSystemTableData(param){
    return request('/cloud/management/tenant/system/cloud/pageList' + sa,{
        body: param
    })
}
//禁用云系统
export async function disableCloudSystemt(param){
    return request('/cloud/management/tenant/system/cloud/disableCloudSys' + sa,{
        body: param
    })
}
//启用云系统
export async function enableCloudSystem(param){
    return request('/cloud/management/tenant/system/cloud/enableCloudSys' + sa,{
        body: param
    })
}

/********************业务系统*********************/
//业务系统列表数据
export async function getBusinessSystemTable(param){
    return request('/cloud/management/tenant/business/pageList' + sa,{
        body: param
    })
}
//新增业务系统
export async function saveBusinessSystem(param){
    return request('/cloud/management/tenant/business/add' + sa,{
        body: param
    })
}
//业务系统code唯一性校验
export async function validateBusinessSystem(param){
    return request('/cloud/management/tenant/business/validate/'+ param.key + sa,{
        body: param.param
    })
}
//业务系统username唯一性校验
export async function validateBusinessSystemAccount(param){
    return request('/cloud/management/user/checkForAdd/userName' + sa,{
        body: param
    })
}
//修改业务系统
export async function updateBusinessSystem(param){
    return request('/cloud/management/tenant/business/update' + sa,{
        body: param
    })
}
/********************云系统*********************/
//云系统列表数据
export async function getCloudSystemTable(param){
    return request('/cloud/management/system/pageList' + sa,{
        body: param
    })
}
//新增云系统
export async function saveCloudSystem(param){
    return request('/cloud/management/system/add' + sa,{
        body: param
    })
}
//云系统code唯一性校验
export async function validateCloudSystem(param){
    return request('/cloud/management/system/validate/'+ param.key + sa,{
        body: param.param
    })
}
//云系统username唯一性校验
export async function validateCloudSystemAccount(param){
    return request('/cloud/management/user/checkForAdd/userName' + sa,{
        body: param
    })
}
//修改云系统
export async function updateCloudSystem(param){
    return request('/cloud/management/system/update' + sa,{
        body: param
    })
}
/********************部门*********************/
//部门列表数据
export async function getDeptTable(param){
    return request('/cloud/management/dept/pageList' + sa,{
        body: param
    })
}
//新增部门
export async function saveDept(param){
    return request('/cloud/management/dept/addDtl' + sa,{
        body: param
    })
}
//部门code唯一性校验(新增)
export async function validateDeptForAdd(param){
    return request('/cloud/management/dept/checkForAdd/'+ param.key + sa,{
        body: param.param
    })
}
//部门code唯一性校验(更新)
export async function validateDeptForUpdate(param){
    return request('/cloud/management/dept/checkForUpdate/'+ param.key + sa,{
        body: param.param
    })
}

//修改部门
export async function updateDept(param){
    return request('/cloud/management/dept/updateDtl' + sa,{
        body: param
    })
}

//删除部门
export async function deleteDept(param){
    return request('/cloud/management/dept/deleteDept' + smvc,{
        body: param
    })
}
//获取部门详情
export async function getDeptDtoById(param){
    return request('/cloud/management/dept/requestData' + sa,{
        body: param
    })
}
//获取参数列表
export async function getParamsValue(param){
    return request('/cloud/management/tenant/paramSetting/loadSingleParam' + sa,{
        body: param
    })
}
/********************组织机构*********************/
//组织机构列表数据
export async function getOrgTable(param){
    return request('/cloud/management/org/pageList' + sa,{
        body: param
    })
}
//组织机构树
export async function getOrgTree(param){
    return request('/cloud/management/org/loadTree' + sa,{
        body: param
    })
}
//新增组织机构
export async function saveOrg(param){
    return request('/cloud/management/org/addDtl' + sa,{
        body: param
    })
}
//组织机构code唯一性校验(新增)
export async function validateOrgForAdd(param){
    return request('/cloud/management/org/checkForAdd/'+ param.key + sa,{
        body: param.param
    })
}
//组织机构code唯一性校验(更新)
export async function validateOrgForUpdate(param){
    return request('/cloud/management/org/checkForUpdate/'+ param.key + sa,{
        body: param.param
    })
}

//修改组织机构
export async function updateOrg(param){
    return request('/cloud/management/org/updateDtl' + sa,{
        body: param
    })
}

//删除组织机构
export async function deleteOrg(param){
    return request('/cloud/management/org/deleteOrg' + smvc,{
        body: param
    })
}
//组织机构详情，by id
export async function getOrgDtoById(param){
    return request('/cloud/management/org/loadCloudOrgDtl' + sa,{
        body: param
    })
}
/********************菜单*********************/
//菜单列表数据
export async function getMenuTable(param){
    return request('/cloud/management/menu/pageList' + sa,{
        body: param
    })
}
//菜单树
export async function getMenuTree(param){
    return request('/cloud/management/menu/loadMenuTree' + sa,{
        body: param
    })
}
//新增菜单
export async function saveMenu(param){
    return request('/cloud/management/menu/add' + sa,{
        body: param
    })
}
//菜单code,name唯一性校验
export async function validateMenu(param){
    return request('/cloud/management/menu/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改菜单
export async function updateMenu(param){
    return request('/cloud/management/menu/update' + sa,{
        body: param
    })
}

//删除菜单(批量)
export async function deletesMenu(param){
    return request('/cloud/management/menu/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//菜单详情by Id
export async function getMenuDtoById(param){
    return request('/cloud/management/menu/getmenu' + sa,{
        body: param
    })
}
/********************参数组*********************/
//参数组列表数据
export async function getParamGroupTable(param){
    return request('/cloud/management/paramGroup/pageList' + sa,{
        body: param
    })
}
//参数组树
export async function getParamGroupTree(param){
    return request('/cloud/management/paramGroup/loadTree' + sa,{
        body: param
    })
}
//新增参数组
export async function saveParamGroup(param){
    return request('/cloud/management/paramGroup/save' + sa,{
        body: param
    })
}
//参数组code,name唯一性校验
export async function validateParamGroup(param){
    return request('/cloud/management/paramGroup/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改参数组
export async function updateParamGroup(param){
    return request('/cloud/management/paramGroup/update' + sa,{
        body: param
    })
}

//删除参数组
export async function deleteParamGroup(param){
    return request('/cloud/management/paramGroup/delete' + sa,{
        body: param
    })
}
/********************参数模板*********************/
//参数模板列表数据
export async function getParamTypeTable(param){
    return request('/cloud/management/paramType/pageList' + sa,{
        body: param
    })
}
//新增参数模板
export async function saveParamType(param){
    return request('/cloud/management/paramType/save' + sa,{
        body: param
    })
}
//参数模板code,name唯一性校验
export async function validateParamType(param){
    return request('/cloud/management/paramType/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改参数模板
export async function updateParamType(param){
    return request('/cloud/management/paramType/update' + sa,{
        body: param
    })
}

//删除参数模板
export async function deleteParamType(param){
    return request('/cloud/management/paramType/delete' + sa,{
        body: param
    })
}

//参数类型值列表数据
export async function getParamValueTable(param){
    return request('/cloud/management/paramSetting/pageList' + sa,{
        body: param
    })
}
//新增参数类型值
export async function saveParamValue(param){
    return request('/cloud/management/paramSetting/save' + sa,{
        body: param
    })
}

//修改参数类型值
export async function updateParamValue(param){
    return request('/cloud/management/paramSetting/update' + sa,{
        body: param
    })
}

//删除参数类型值
export async function deleteParamValue(param){
    return request('/cloud/management/paramSetting/deletes' + sa,{
        body: param
    })
}

/********************租户参数*********************/
//参数类型值列表数据
export async function getTenantParamValueTable(param){
    return request('/cloud/management/tenant/paramSetting/pageList' + sa,{
        body: param
    })
}
//新增参数类型值
export async function saveTenantParamValue(param){
    return request('/cloud/management/tenant/paramSetting/save' + sa,{
        body: param
    })
}

//修改参数类型值
export async function updateTenantParamValue(param){
    return request('/cloud/management/tenant/paramSetting/update' + sa,{
        body: param
    })
}

//删除参数类型值
export async function deleteTenantParamValue(param){
    return request('/cloud/management/tenant/paramSetting/deletes' + sa,{
        body: param
    })
}
/********************租户常量*********************/
//租户常量列表数据
export async function getConstantTable(param){
    return request('/cloud/management/tenant/constant/pageList' + sa,{
        body: param
    })
}

//新增租户常量
export async function saveConstant(param){
    return request('/cloud/management/tenant/constant/add' + sa,{
        body: param
    })
}
//租户常量code,name唯一性校验
export async function validateConstant(param){
    return request('/cloud/management/tenant/constant/validate/'+ param.key + sa,{
        body: param.param
    })
}

//修改租户常量
export async function updateConstant(param){
    return request('/cloud/management/tenant/constant/update' + sa,{
        body: param
    })
}
/********************租户人员维护*********************/
export async function getTenantStaffTableData(param){
    return request('/cloud/management/staff/tenant/pageListWithPermission' + sa,{
        body: param
    })
}
//权限机构部门树
export async function loadOrgTreeByPermission(param){
    return request('/cloud/management/org/loadOrgTreeByPermission' + sa,{
        body: param
    })
}
//单个参数值获取
export async function getSingleParam(param){
    return request('/cloud/management/tenant/paramSetting/loadSingleParam' + sa,{
        body: param
    })
}
//多个参数值获取
export async function getMultiParam(param){
    return request('/cloud/management/tenant/paramSetting/loadMultiParamList' + sa,{
        body: param,
        contentType:ContentType.JSON
    })
}
//租户人员code唯一性校验
export async function validateTenantStaff(param){
    return request('/cloud/management/staff/tenant/checkForAdd/'+ param.key + sa,{
        body: param.param
    })
}
//租户人员phone唯一性校验
export async function validateStaffPhone(param){
    return request('/cloud/management/staff/system/resetTenantUserPassword/'+ param.key + sa,{
        body: param.param
    })
}
//新增租户人员
export async function saveTenantStaff(param){
    return request('/cloud/management/staff/tenant/add' + sa,{
        body: param
    })
}
//修改租户人员
export async function updateTenantStaff(param){
    return request('/cloud/management/staff/tenant/update' + sa,{
        body: param
    })
}
//删除租户人员
export async function deletesTenantStaff(param){
    return request('/cloud/management/staff/tenant/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//获取租户人员详情
export async function getTenantStaffDtoById(param){
    return request('/cloud/management/staff/tenant/loadStaffDtl' + sa,{
        body: param
    })
}

//开启用户
export async function saveUser(param){
    return request('/cloud/management/user/add' + sa,{
        body: param
    })
}
//修改用户
export async function updateUser(param){
    return request('/cloud/management/user/update' + sa,{
        body: param
    })
}
//重置用户密码
export async function resetUserPassword(param){
    return request('/cloud/management/user/resetPassword' + sa,{
        body: param
    })
}
//获取用户详情
export async function getUserById(param){
    return request('/cloud/management/user/loadCloudUserDtl' + sa,{
        body: param
    })
}
//获取用户权限列表
export async function getPermissionScopeList(param){
    return request('/cloud/management/user/loadPermissionScope' + sa,{
        body: param
    })
}
//导入
export async function importStaffData(param){
    return requestFile('/cloud/management/staff/tenant/uploadImportData',{
        method: 'POST',
        body:param.param,
        dataType: 'text',
        contentType: false
    })
}
//获取导入结果列表
export async function getImportTableData(param){
    return request('/cloud/management/uploadResultInfo/queryList' + smvc,{
        body: param
    })
}
//获取用户部门机构树
export async function loadDeptOrgTree(param){
    return request('/cloud/management/org/loadDeptOrgTree' + sa,{
    })
}
/********************业务系统人员维护*********************/
export async function getSystemStaffTableData(param){
    return request('/cloud/management/staff/system/pageListWithPermission' + sa,{
        body: param
    })
}
//业务系统人员code唯一性校验
export async function validateSystemStaff(param){
    return request('/cloud/management/staff/system/checkForAdd/'+ param.key + sa,{
        body: param.param
    })
}
//新增业务系统人员
export async function saveSystemStaff(param){
    return request('/cloud/management/staff/system/add' + sa,{
        body: param
    })
}
//修改业务系统人员
export async function updateSystemStaff(param){
    return request('/cloud/management/staff/system/update' + sa,{
        body: param
    })
}
//删除业务系统人员
export async function deletesSystemStaff(param){
    return request('/cloud/management/staff/system/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//获取业务系统人员详情
export async function getSystemStaffDtoById(param){
    return request('/cloud/management/staff/system/loadStaffDtl' + sa,{
        body: param
    })
}
//导入
export async function importSystemStaffData(param){
    return requestFile('/cloud/management/staff/system/uploadImportData',{
        method: 'POST',
        body:param.param,
        dataType: 'text',
        contentType: false
    })
}
//导出
export async function exportStaff(param){
    return downloadFile('/cloud/management/staff/tenant/download' + sa,{
        body:param
    })
}
export async function loadRoleStyle(param){
    return request('/cloud/management/user/'+param.userId+'/role/getUserRoleStyle' + sa,{})
}
//系统角色树
export async function loadSystemRoleTree(param){
    return request('/cloud/management/role/loadSystemRoleTree' + sa,{})
}
//用户角色关系数据
export async function getUserRoleRelation(param){
    return request('/cloud/management/user/'+param.userId+'/role/dataList' + sa,{})
}
//租户角色树
export async function loadTenantRoleTree(param){
    return request('/cloud/management/tenant/role/loadRoleTree' + sa,{})
}
//租户角色关系数据
export async function getTenantUserRoleRelation(param){
    return request('/cloud/management/user/'+param.userId+'/role/dataTenantList' + sa,{})
}
//角色功能关系保存
export async function userRoleTreeDataSave(param){
    return request('/cloud/management/user/' + param.userId+ '/role/add' + sa,{
        body: param
    })
}
/********************租户行政区划*********************/
export async function getDivisionTenantTable(param){
    return request('/cloud/management/tenant/xzqh/pageList' + sa,{
        body: param
    })
}
export async function getDivisionTenantTree(param){
    return request('/cloud/management/tenant/xzqh/loadTree' + sa,{
        body: param
    })
}
export async function saveDivisionTenant(param){
    return request('/cloud/management/tenant/xzqh/add' + sa,{
        body: param
    })
}
export async function updateDivisionTenant(param){
    return request('/cloud/management/tenant/xzqh/update' + sa,{
        body: param
    })
}
export async function deletesDivisionTenant(param){
    return request('/cloud/management/tenant/xzqh/cascadeDeleteChildren' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
export async function batchDivisionTenant(param){
    return request('/cloud/management/tenant/xzqh/batchUpdate' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
export async function getDivisionTenantDtl(param){
    return request('/cloud/management/tenant/xzqh/loadTenantDivisionDtl' + sa,{
        body: param
    })
}
export async function DivisionCommonCode(param){
    return request('/cloud/management/tenant/xzqh/checkForm/commonCode' + sa,{
        body: param
    })
}
/********************行政区划模板*********************/
export async function getDivisionTemplateTable(param){
    return request('/cloud/management/xzqh/pageList' + sa,{
        body: param
    })
}
export async function loadTreeAsync(param){
    return request('/cloud/management/xzqh/loadTree' + sa,{
        body: param
    })
}
export async function saveDivisionTemplate(param){
    return request('/cloud/management/xzqh/add' + sa,{
        body: param
    })
}
export async function updateDivisionTemplate(param){
    return request('/cloud/management/xzqh/update' + sa,{
        body: param
    })
}
export async function deletesDivisionTemplate(param){
    return request('/cloud/management/xzqh/cascadeDeleteChildren' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
export async function batchDivisionTemplate(param){
    return request('/cloud/management/xzqh/batchUpdate' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
export async function getTemplateDetailByid(param){
    return request('/cloud/management/xzqh/loadDivisionDtl' + sa,{
        body: param
    })
}
export async function TemplateCommonCode(param){
    return request('/cloud/management/xzqh/checkForm/commonCode' + sa,{
        body: param
    })
}

/********************图元类型*********************/
export async function getWorkElementTypeTable(param){
    return request('/cloud/management/workElementType/pageListByPermission' + read,{
        body: param
    })
}
export async function getOrgTreeDataWithPermission(param){
    return request('/cloud/management/org/loadOrgTreeByPermission' + sa,{
        body: param
    })
}
export async function validateWorkElementTypeName(param){
    return request('/cloud/management/workElementType/checkForm/name' + read,{
        body: param
    })
}
export async function validateWorkElementTypeCode(param){
    return request('/cloud/management/workElementType/checkForm/code' + read,{
        body: param
    })
}
export async function saveWorkElementType(param){
    return request('/cloud/management/workElementType/save.edit',{
        body: param
    })
}
export async function updateWorkElementType(param){
    return request('/cloud/management/workElementType/update.edit',{
        body: param
    })
}
export async function deletesWorkElementType(param){
    return request('/cloud/management/workElementType/deletes.edit',{
        body: param,
        contentType: ContentType.JSON
    })
}
export async function WorkElementTypeById(param){
    return request('/cloud/management/workElementType/findbyid/'+param.id+'.read',{
        body: {},
    })
}
/********************图元管理*********************/
//图元类型
export async function getWorkElementTypeList(param){
    return request('/cloud/management/workElementType/loadWorkElementTypeWithPermission.read',{
        body: param
    })
}
export async function getWorkElementTable(param){
    return request('/cloud/management/workElement/pageListByPermission.read',{
        body: param
    })
}
export async function validateWorkElementCode(param){
    return request('/cloud/management/workElement/checkForm/code' + read,{
        body: param
    })
}
export async function validateWorkElementName(param){
    return request('/cloud/management/workElement/checkForm/name' + read,{
        body: param
    })
}
export async function saveWorkElement(param){
    return request('/cloud/management/workElement/save.edit',{
        body: param
    })
}
export async function updateWorkElement(param){
    return request('/cloud/management/workElement/update.edit',{
        body: param
    })
}
export async function deletesWorkElement(param){
    return request('/cloud/management/workElement/deletes.edit' ,{
        body: param,
        contentType: ContentType.JSON
    })
}
//获取树根节点
export async function getRootDivisionId(param){
    return request('/cloud/management/xzqh/getRootDivisionId.sa',{
        body: param
    })
}
//导入
export async function importWorkElementData(param){
    return requestFile('/cloud/management/workElement/uploadImportData?typeCode='+param.typeCode,{
        method: 'POST',
        body:param.param,
        dataType: 'text',
        contentType: false
    })
}
//导出
export async function exportWorkelement(param){
    return downloadFile('/cloud/management/workElement/download',{
        body:param
    })
}
/********************图元GIS管理*********************/
export async function getOrgWorkElementTree(param){
    return request('/cloud/management/workElement/loadCompanySectionTreeByPermission.read',{
        body: param
    })
}
export async function deleteWorkElement(param){
    return request('/cloud/management/workElement/delete/'+param.id+'.edit' ,{
    })
}
export async function updateWorkElementGis(param){
    return request('/cloud/management/workElement/update' +'.edit' ,{
        body: param
    })
}

/********************拷贝菜单资源*********************/
export async function getCustomValue(param){
    return request('/cloud/management/web/customproperties/getCustomValue.smvc',{
        body: param
    })
}
export async function getSourceTenantList(param){
    return request('/cloud/management/tenant/getTenantList.sa',{
        body: param
    })
}
export async function getSourceSystemList(param){
    return request('/cloud/management/tenant/business/getSystemList.sa',{
        body: param
    })
}
export async function getSourceMenuTree(param){
    return request('/cloud/management/menu/loadMenuTree.sa',{
        body: param
    })
}
export async function getTargetTenantList(param){
    return request('/cloud/management/ds2/tenant/getTenantList.sa',{
        body: param
    })
}
export async function getTargetSystemList(param){
    return request('/cloud/management/tenant/ds2/business/getSystemList.sa',{
        body: param
    })
}
export async function getTargetMenuTree(param){
    return request('/cloud/management/ds2/menu/loadMenuTree.sa',{
        body: param
    })
}
export async function startToCopy(param){
    return request('/cloud/management/copyresource/copy.smvc',{
        body: param
    })
}
export async function getLoginLogTable(param){
    return request('/cloud/management/log/pageList.sa',{
        body: param
    })
}
//绑定租户
export async function getAllTenantList(param){
    return request('/cloud/management/rest/np/tenantrelation/listExceptViceTenant',{
        body: param,
        defaultUrl: 'http://192.168.1.215:8089'
    })
}
export async function getHadBindTenantList(param){
    return request('/cloud/management/rest/np/tenantrelation/listViceTenant',{
        body: param,
        defaultUrl: 'http://192.168.1.215:8089'
    })
}
export async function bindTenantSaving(param){
    return request('/cloud/management/rest/np/tenantrelation/bandingRelation',{
        body: param,
        defaultUrl: 'http://192.168.1.215:8089'
    })
}

/********************租户角色组*********************/
//角色组列表数据
export async function getTenantRoleGroupTable(param){
    return request('/cloud/management/tenant/rolegroup/pageList' + sa,{
        body: param
    })
}
//角色组树
export async function getTenantRoleGroupTree(param){
    return request('/cloud/management/tenant/rolegroup/loadTree' + sa,{
        body: param
    })
}
//新增角色组
export async function saveTenantRoleGroup(param){
    return request('/cloud/management/tenant/rolegroup/save' + sa,{
        body: param
    })
}
//角色组code,name唯一性校验
export async function validateTenantRoleGroup(param){
    return request('/cloud/management/tenant/rolegroup/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改角色组
export async function updateTenantRoleGroup(param){
    return request('/cloud/management/tenant/rolegroup/update' + sa,{
        body: param
    })
}
//删除角色组
export async function deleteTenantRoleGroup(param){
    return request('/cloud/management/tenant/rolegroup/delete/'+ param + sa,{
        body: {}
    })
}
//删除角色组(批量)
export async function deletesTenantRoleGroup(param){
    return request('/cloud/management/tenant/rolegroup/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
/********************角色*********************/
//角色列表数据
export async function getTenantRoleTable(param){
    return request('/cloud/management/tenant/role/pageList' + sa,{
        body: param
    })
}

//新增角色
export async function saveTenantRole(param){
    return request('/cloud/management/tenant/role/save' + sa,{
        body: param
    })
}
//角色code,name唯一性校验
export async function validateTenantRole(param){
    return request('/cloud/management/tenant/role/checkForm/'+ param.key + sa,{
        body: param.param
    })
}

//修改角色
export async function updateTenantRole(param){
    return request('/cloud/management/tenant/role/update' + sa,{
        body: param
    })
}

//删除角色(批量)
export async function deletesTenantRole(param){
    return request('/cloud/management/tenant/role/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//角色详情by Id
export async function getTenantRoleDtoById(param){
    return request('/cloud/management/tenant/role/loadById' + sa,{
        body: param
    })
}
//功能树By tenantId
export async function loadTreeByTenantId(param){
    return request('/cloud/management/function/loadTreeByTenantId' + sa,{
        body: param
    })
}
//角色功能关系数据
export async function getTenantRoleFunRelation(param){
    return request('/cloud/management/tenant/functionrole/dataList' + sa,{
        body: param
    })
}
//角色功能关系保存
export async function tenantRoleBindFunctionTreeSave(param){
    return request('/cloud/management/tenant/functionrole/save/' + param.id + sa,{
        body: param.param
    })
}

//根据功能码列表获取功能权限map
export async function getFunctionPermissionMap(param){
    return request('/cloud/management/rest/permission/hasFunctions',{
        body: param
    })
}
//将系统码转化为系统id
export async function convertSystemCodeToId(param){
    return request('/cloud/management/util/getSystemIdByCode',{
        body: param
    })
}

/********************租户用户管理*********************/
//角色列表数据
export async function getTenantUserTable(param){
    return request('/cloud/management/portalUser/pageList' + sa,{
        body: param
    })
}

//新增角色
export async function saveTenantUser(param){
    return request('/cloud/management/portalUser/save' + sa,{
        body: param
    })
}

//修改角色
export async function updateTenantUser(param){
    return request('/cloud/management/portalUser/update' + sa,{
        body: param
    })
}

//删除角色(批量)
export async function deletesTenantUser(param){
    return request('/cloud/management/portalUser/deletes' + sa,{
        body: param,
        contentType: ContentType.JSON
    })
}
//角色详情by Id
export async function getTenantUserDtoById(param){
    return request('/cloud/management/portalUser/loadById' + sa,{
        body: param
    })
}
//校验手机
export async function validatePhone(param){
    return request('/cloud/management/portalUser/checkForm/'+ param.key + sa,{
        body: param
    })
}
//重置租户人员密码
export async function resetTenantUserPassword(param){
    return request('/cloud/management/portalUser/resetPassword' + sa,{
        body: param
    })
}
//获取租户信息
export async function getLoginInfo(param){
    return request('/cloud/management/util/logininfo' + sa,{
        body: param
    })
}