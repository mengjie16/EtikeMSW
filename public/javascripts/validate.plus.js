/* 设置校验框架默认配置参数 */
$.validator.setDefaults({
	onsubmit: false,
	ignoreTitle: true,
	errorElement: 'span',
	errorPlacement: function(error, element) {
		var $con = $('div[for="' + element.attr('id') + '"]');
		if ($con.length) {
			//error.appendTo($con);
			$con.html(error);
			return;
		}
		element.after(error);
	},
	success: function(label, element) {
		var $con = $('div[for="' + $(element).attr('id') + '"]');
		if ($con.length) {
			$con.html('<i class="iconfont" style="color:#8CD030;font-size:18px;">&#xe609;</i>');
			return;
		}
	}
});

Tr.error = $.validator.format('<span style="color:#EB5635;">{0}</span>');
Tr.errorLeft = $.validator.format('<span class="floatLeft" style="color:#EB5635;">{0}</span>');


Tr.regs = {
	nick: /^[\u4e00-\u9fa5a-zA-Z0-9]{4,15}$/,
	password: /^\w{6,20}$/,
	alphanumeric: /^[0-9a-zA-Z]+$/,
	chinese: /^[\u4E00-\u9FA5\uF900-\uFA2D]+$/,
	ascii: /^[\x00-\xFF]+$/,
	qq: /^[1-9]{1}[0-9]{4,10}$/,
	mobile: /^0{0,1}(13[0-9]|14[5-7]|15[0-3]|15[5-9]|18[0-9]|17[0-9])[0-9]{8}$/,
	tel: /^(([0\+]\d{2,3}-)?(0\d{2,3})-)?(\d{6,9})(-(\d{3,}))?$/,
	mobileCaptcha: /^\d{6}$/,
	imgCaptcha: /^[0-9a-zA-Z]{4}$/,
	username: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
	idcard: /^[1-9]([0-9]{14}|[0-9]{17}|[0-9]{16}[xX])$/,
	bank: /^\d{16,19}$/,
	alipay: /^(0{0,1}(13[0-9]|14[6|7]|15[0-3]|15[5-9]|18[0-9])[0-9]{8}||([a-zA-Z0-9]+[_|\_|\-|\.]?)*@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3})$/,
	tenpay: /^(0{0,1}(13[0-9]|14[6|7]|15[0-3]|15[5-9]|18[0-9])[0-9]{8}||([a-zA-Z0-9]+[_|\_|\-|\.]?)*@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}||[1-9]{1}[0-9]{4,10})$/,
	keyword: /^(([\u4E00-\u9FA5\x00-\xff]+)|([\u4E00-\u9FA5\x00-\xff]+\s+[\u4E00-\u9FA5\x00-\xff]+))$/,
	empty: /^\S+$/,
	times: /^((((1[6-9]|[2-9]\d)\d{2})-(0[13578]|1[02])-(0[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0[13456789]|1[012])-(0[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\d):[0-5]?\d:[0-5]?\d$/,
	truename:/^([\u4e00-\u9fa5]+|([a-zA-Z]+?.\s?)+)$/,
	url:/^https?:\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/
};


/* 修改校验框架默认提示信息 */
$.extend($.validator.messages, {
	required: Tr.error('未填写'),
	email: Tr.error('Email 格式不正确'),
	url: Tr.error('格式不正确'),
	number: Tr.error('{0}必须为数字'),
	digits: Tr.error('必须为整数'),
	alphanumeric: Tr.error('必须为字母或数字'),
	length: Tr.error('长度必须为{0}'),
	maxlength: Tr.error('不能超过{0}个字符'),
	minlength: Tr.error('至少{0}个字符'),
	rangelength: Tr.error('长度只能为 {0} 到 {1} 个字符'),
	range: Tr.error('只能输入 {0} 到 {1} 之间的数值'),
	max: Tr.error('最大不能超过{0}'),
	min: Tr.error('不能小于{0}'),
	nick: Tr.error('昵称只能由英文字母及数字组成'),
	qq: Tr.error('QQ号码不正确'),
	mobile: Tr.error('手机号码不正确'),
	mobileCaptcha: Tr.error('手机验证码不正确'),
	imgCaptcha: Tr.error('图片验证码不正确'),
	maxwords: Tr.error('不能超过{0}个字'),
	minwords: Tr.error('不能少于{0}个字'),
	price: Tr.error('价格不正确'),
	remote: "Please fix this field",
	date: "日期格式不正确",
	dateISO: "日期格式不正确",
	equalTo: "Please enter the same value again.",
	checkContact: Tr.error('号码不正确'),
	times:Tr.error('日期格式不正确'),
	username:Tr.error('用户名不正确'),
	truename:Tr.error('姓名不正确'),
	password:Tr.error('密码格式不正确')
});


/* 增加额外校验方法 */
$.validator.methods.exactLength = function(value, element, param) {
	var length = $.isArray( value ) ? value.length : $.validator.prototype.getLength( $.trim( value ), element );
	return length == param;
};
$.validator.methods.nick = function(value, element, param) {
	return Tr.regs.nick.test(value);
};
$.validator.methods.password = function(value, element, param) {
	return Tr.regs.password.test(value);
};
$.validator.methods.alphanumeric = function(value, element, param) {
	return Tr.regs.alphanumeric.test(value);
};
$.validator.methods.chinese = function(value, element, param) {
	return Tr.regs.chinese.test(value);
};
$.validator.methods.ascii = function(value, element, param) {
	return Tr.regs.ascii.test(value);
};
$.validator.methods.qq = function(value, element, param) {
	return Tr.regs.qq.test(value);
};
$.validator.methods.mobile = function(value, element, param) {
	return Tr.regs.mobile.test(value);
};
$.validator.methods.tel = function(value, element, param) {
	return Tr.regs.tel.test(value);
};
$.validator.methods.mobileCaptcha = function(value, element, param) {
	return Tr.regs.mobileCaptcha.test(value);
};
$.validator.methods.imgCaptcha = function(value, element, param) {
	return Tr.regs.imgCaptcha.test(value);
};
$.validator.methods.username = function(value, element, param) {
	return Tr.regs.username.test(value);
};
$.validator.methods.truename = function(value, element, param) {
	return Tr.regs.truename.test(value);
};
$.validator.methods.idcard = function(value, element, param) {
	return Tr.regs.idcard.test(value);
};
$.validator.methods.keyword = function(value, element, param) {
	return Tr.regs.keyword.test(value);
};
$.validator.methods.times = function(value, element, param) {
	return Tr.regs.times.test(value);
};
$.validator.methods.checkContact = function(value, element, param) {
	if(Tr.regs.mobile.test(value)){
  	return true;
  }else if(Tr.regs.tel.test(value)){
  	return true;
  }
};
/*
	半角范围：u0000 - u00FF, uFF61 - uFF9F, uFFE8 - uFFEE 
	全角范围： 
	全角数字(0-9) uFF10 - uFF19 
	全角大文字(A-Z): uFF21 - uFF3A 
	全角小文字(a-z): uFF41 - uFF5A 
	全角平仮名：u3040 - u309F 
	全角片仮名：u30A0 - u30FF 
	全角Latin: uFF01 - uFF5E 
	全角Symbol: uFFE0 - uFFE5
	以下是判断全角半角混合的宝贝标题字符串的字数。(2个字节为1个字，半角被认为是1个字节)
*/
Tr.countWords = function(text) {
	var count = 0;
	var uFF61 = parseInt("FF61", 16);
	var uFF9F = parseInt("FF9F", 16);
	var uFFE8 = parseInt("FFE8", 16);
	var uFFEE = parseInt("FFEE", 16);
	for (var i = 0; i < text.length; i++) {
		var c = parseInt(text.charCodeAt(i));
		if (c <= 255) {
			count++;
			continue;
		}
		if ((uFF61 <= c) && (c <= uFF9F)) {
			count++;
			continue;
		}
		if ((uFFE8 <= c) && (c <= uFFEE)) {
			count++;
			continue;
		}
		count = count + 2;
	}
	return Math.round(count / 2);
};

$.validator.methods.maxwords = function(value, element, param) {
	return Tr.countWords(value) <= param;
};
$.validator.methods.minwords = function(value, element, param) {
	return Tr.countWords(value) >= param;
};
//相同name的条件下的校验
$(function () {
if ($.validator) {
    //fix: when several input elements shares the same name, but has different id-ies....
    $.validator.prototype.elements = function () {
        var validator = this,
            rulesCache = {};
        // select all valid inputs inside the form (no submit or reset buttons)
        // workaround $Query([]).add until http://dev.jquery.com/ticket/2114 is solved
        return $([]).add(this.currentForm.elements)
        .filter(":input")
        .not(":submit, :reset, :image, [disabled]")
        .not(this.settings.ignore)
        .filter(function () {
            var elementIdentification = this.id || this.name;
            !elementIdentification && validator.settings.debug && window.console && console.error("%o has no id nor name assigned", this);
            // select only the first element for each name, and only those with rules specified
            if (elementIdentification in rulesCache || !validator.objectLength($(this).rules()))
                return false;
            rulesCache[elementIdentification] = true;
            return true;
        });
    };
}
});