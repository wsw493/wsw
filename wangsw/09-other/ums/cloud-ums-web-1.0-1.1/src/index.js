import './index.html';
import dva from 'dva';
import './index.less';

// 1. Initialize
const app = dva();

// 2. Plugins
// app.use({});

// 3. Model
// app.model(require('./models/example'));
//yzq
app.model(require('./models/workElementGisM/workElementGisM'));
app.model(require('./models/tenantM/tenantM'));
app.model(require('./models/roleGroupM/roleGroupM'));
app.model(require('./models/functionGroupM/functionGroupM'));
app.model(require('./models/systemM/cloud/cloudSystemM'));
app.model(require('./models/systemM/business/businessSystemM'));
app.model(require('./models/orgM/orgM'));
app.model(require('./models/functionsM/functionsM'));
app.model(require('./models/roleM/roleM'));
app.model(require('./models/menuM/menuM'));
app.model(require('./models/tenantM/constantM/constantM'));
app.model(require('./models/staffM/systemStaffM'));
app.model(require('./models/copyResourceM/copyMenuResourceM'));
app.model(require('./models/loginLogM/loginLogM'));
app.model(require('./models/bindTenantM/bindTenantM'));
app.model(require('./models/tenantM/roleGroupM/roleGroupM'));
app.model(require('./models/tenantM/roleM/roleM'));
app.model(require('./models/tenantM/userM/userM'));

//zzz
app.model(require('./models/paramGroupM/paramGroupM'));
app.model(require('./models/paramM/paramM'));
app.model(require('./models/tenantM/paramM/paramM'));
app.model(require('./models/deptM/deptM'));
app.model(require('./models/staffM/tenantStaffM'));
app.model(require('./models/tenantM/xzqhM/xzqhM'));
app.model(require('./models/xzqhM/xzqhM'));
app.model(require('./models/workElementTypeM/workElementTypeM'));
app.model(require('./models/workElementM/workElementM'));

// 4. Router
app.router(require('./router'));

// 5. Start
app.start('#root');
