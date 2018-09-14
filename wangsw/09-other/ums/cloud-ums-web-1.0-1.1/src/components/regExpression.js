import {formValidation} from '../utils/toolFunctions';
/**/

export function validate(regList,value){
	let vtx=''
	let vtxArr = [];
	vtxArr = regList.map((item)=>{
		switch(item){
			case 'empty':
				vtx = formValidation({checkType:'empty', checkVal:value })?'空':'';
				break;
			case 'required':
				vtx = formValidation({checkType:'required', checkVal:value })?'':'必填项';
				break;
			case 'number':
				vtx = formValidation({checkType:'number', checkVal:value })?'':'只能是数字';
				break;
			case 'codeValidator':
				vtx = formValidation({checkType:'codeValidator', checkVal:value })?'':'最长255位';
				break;
			case 'nameValidator':
				vtx = formValidation({checkType:'nameValidator', checkVal:value })?'':'最长255位';
				break;
			case 'email':
				vtx = formValidation({checkType:'email', checkVal:value })?'':'邮箱格式错误';
				break;
			case 'phone':
				vtx = formValidation({checkType:'phone', checkVal:value })?'':'电话格式错误';
				break;
			case 'idCard':
				vtx = formValidation({checkType:'idCard', checkVal:value })?'':'身份证格式错误';
				break;
			case 'officePhone':
				vtx = formValidation({checkType:'officePhone', checkVal:value })?'':'电话格式错误';
				break;
		}
		return vtx;
	});
	let arr = [];
	vtxArr.map((item)=>{
		if(item != ""){
			arr.push(item);
		}
	});
	return arr.length==0 || arr[0]=='空'?'':arr[0];
}
