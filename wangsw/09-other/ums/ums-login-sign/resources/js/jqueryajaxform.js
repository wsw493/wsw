JqueryAjaxForm = function(config) {
	config = config || {};

	this.formId = config.formId || 'form';
	this.formUrl = config.actionUrl;
	// jquery-validation or jquery-easyui
	this.formValidType = config.formValidType || 'jquery-validation';

	this.formShowRequest = config.formShowRequest;
	this.formShowResponse = config.formShowResponse;

	this.customFormValid = config.customFormValid;
	this.successCallback = config.successCallback;

	this.beenNeedValid = true;
	if (config.beenNeedValid != undefined){
		this.beenNeedValid  = config.beenNeedValid;
	}
	this.beenNeedBindForm = true;
	if (config.beenNeedBindForm != undefined){
		this.beenNeedBindForm = config.beenNeedBindForm;
	}
	this.init();
};
JqueryAjaxForm.prototype.init = function() {
	var t = this;

	var defaultFormShowRequest = function(formData, jqForm, options) {
		// 去除空格
		$(formData).each(function(i, n) {
			n.value = n.value.trim();
		});

		if (t.beenNeedValid){
			return t.validForm();
		} else {
			return true;
		}

	};

	var defaultFormShowResponse = function(responseText, statusText, xhr, $form) {
		VortexUtil.show({
			msg : responseText.operateMessage
		});
		if (responseText.operateSuccess) {
			if (typeof t.successCallback == 'function') {
				t.successCallback(responseText);
			}
		}

	};
	var options = {
		target : '#output_' + t.formId, // target element(s) to be updated with
		// server
		// response
		beforeSubmit : t.formShowRequest ? t.formShowRequest
				: defaultFormShowRequest, // pre-submit callback
		success : t.formShowResponse ? t.formShowResponse
				: defaultFormShowResponse,
		dataType : 'json',
		type : 'post'
	};

	if (t.formUrl){
		options.url = t.formUrl;
	}
	if (t.beenNeedBindForm){
		// bind to the form's submit event
		$('#' + t.formId).submit(function() {
			$(this).ajaxSubmit(options);

			return false;
		});
	} else {
		$('#' + t.formId).ajaxSubmit(options);
	}
	

};
JqueryAjaxForm.prototype.validForm = function() {
	var t = this;
	if (typeof t.customFormValid == 'function') {
		return t.customFormValid();
	}
	if (t.formValidType == 'jquery-validation') {
		return $('#' + t.formId).valid();
	} else if (t.formValidType == 'jquery-easyui') {
		return $("#" + t.formId).form('validate');
	} else {
		return true;
	}
};
