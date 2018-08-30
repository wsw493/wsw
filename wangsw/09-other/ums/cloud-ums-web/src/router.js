import React from 'react';
import { Router, Route } from 'dva/router';
import IndexPage from './routes/IndexPage';
//yzq
import Tenant from './routes/tenant/tenant';//租户管理
import RoleGroup from './routes/roleGroup/roleGroup';//角色组管理
import FunctionGroup from './routes/functionGroup/functionGroup';//功能组管理
import WorkElementGis from './routes/workElementGis/workElementGis';//图元地图管理
import CloudSystem from './routes/system/cloud/cloudSystem';//云系统管理
import BusinessSystem from './routes/system/business/businessSystem';//业务系统管理
import Org from './routes/org/org';//组织机构管理
import Functions from './routes/functions/functions';//功能管理
import Role from './routes/role/role';//角色管理
import Menu from './routes/menu/menu';//菜单管理
import Constant from './routes/tenant/constant/constant';//租户常量管理
import SystemStaff from './routes/staff/system/systemStaff';//业务系统人员管理
import CopyMenuResource from './routes/copyResource/copyMenuResource';//拷贝菜单资源
import LoginLog from './routes/loginLog/loginLog';//登陆日志
import BindTenant from './routes/bindTenant/bindTenant';//租户绑定租户
import TenantRoleGroup from './routes/tenant/roleGroup/roleGroup';//角色组管理
import TenantRole from './routes/tenant/role/role';//角色管理
import TenantUser from './routes/tenant/user/user';//租户用户管理
import SystemRoleGroup from './routes/systemRoleGroup/roleGroup';//系统角色组管理
import SystemRole from './routes/systemRole/role';//角色管理

//zzz
import ParamGroup from './routes/paramGroup/paramGroup';//参数组
import Param from './routes/param/param';//参数模板
import TenantParam from './routes/tenant/param/param';//租户参数
import Dept from './routes/dept/dept';//部门管理
import TenantStaff from './routes/staff/tenant/tenantStaff';//租户人员管理
import Tenantxzqh from './routes/tenant/xzqh/xzqh';//租户行政区划
import Xzqh from './routes/xzqh/xzqh';//行政区划模板
import WorkElementType from './routes/workElementType/workElementType';//图元类型
import WorkElement from './routes/workElement/workElement';//图元管理
function RouterConfig({ history }) {
  return (
    <Router history={history}>
	    {/*yzq*/}
	    <Route path='/tenant' component={Tenant}/>
	    <Route path='/rolegroup' component={RoleGroup}/>
	    <Route path='/workelementgis' component={WorkElementGis}/>
	    <Route path='/functiongroup' component={FunctionGroup}/>
	    <Route path='/cloudsystem' component={CloudSystem}/>
	    <Route path='/businesssystem' component={BusinessSystem}/>
		<Route path='/org' component={Org} />
		<Route path='/functions' component={Functions} />
		<Route path='/role' component={Role} />
		<Route path='/menu' component={Menu} />
		<Route path='/constant' component={Constant} />
		<Route path='/systemstaff' component={SystemStaff} />
		<Route path='/copymenuresource' component={CopyMenuResource} />
		<Route path='/loginlog' component={LoginLog} />
		<Route path='/bindtenant' component={BindTenant}/>
		<Route path='/tenantrolegroup' component={TenantRoleGroup}/>
		<Route path='/tenantrole' component={TenantRole} />
		<Route path='/tenantuser' component={TenantUser} />
		<Route path='/systemrolegroup' component={SystemRoleGroup} />
		<Route path='/systemrole' component={SystemRole} />
	      
	    {/*zzz*/}
	    <Route path='/paramgroup' component={ParamGroup} />
	    <Route path='/param' component={Param} />
	    <Route path='/tenantparam' component={TenantParam} />
	    <Route path='/dept' component={Dept} />
	    <Route path='/tenantstaff' component={TenantStaff} />
	    <Route path='/tenantxzqh' component={Tenantxzqh} />
	    <Route path='/xzqh' component={Xzqh} />
	    <Route path='/workelementtype' component={WorkElementType} />
	    <Route path='/workelement' component={WorkElement} />

    </Router>
  );
}

export default RouterConfig;
