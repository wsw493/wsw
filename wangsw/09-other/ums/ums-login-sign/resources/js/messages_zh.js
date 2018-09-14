/*
 * Translated default messages for the jQuery validation plugin.
 * Locale: ZH (Chinese, 中文 (Zhōngwén), 汉语, 漢語)
 */
(function ($) {
	$.extend($.validator.messages, {
		required: "必填",
		remote: "修正该字段",
		email: "电子邮件格式",
		url: "网址",
		date: "日期",
		dateISO: "日期 (ISO).",
		number: "数字",
		digits: "整数",
		creditcard: "信用卡号",
		equalTo: "输入相同的值",
		accept: "合法的字符串",
		maxlength: $.validator.format("长度最多是 {0} 的字符串"),
		minlength: $.validator.format("长度最少是 {0} 的字符串"),
		rangelength: $.validator.format("长度介于 {0} 和 {1} 之间的字符串"),
		range: $.validator.format("介于 {0} 和 {1} 之间的值"),
		phone : "格式错误",
		max: $.validator.format("最大为 {0} 的值"),
		min: $.validator.format("最小为 {0} 的值")
	});
}(jQuery));

jQuery.extend(jQuery.validator.defaults, {
    errorElement: "span"
});