$.extend($.validator.messages, {
	required: Tr.error('金额必填'),
	number: Tr.error('必须为数字'),
});
//表单验证
function payValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'fee': {
				required: true,
				number: true
			}
		},
		success: function(label, element) {}
	};
	return options;
}
$(function(){
	//去支付
	$(document).on('click','.btnStd',function(){
		var validator = $('#payForm').validate(payValidateOpts());
		if (!validator.form()) {
			return;
		}
		$('.stepOne').hide().next().show();
	});
	// 提交支付
	$(document).on('click', '#payForm #btnSubmit', function() {
		
		$('#payForm').submit();
	});

	$('#btnShowQrcode').click(function(){
		$('.stepTwo').hide();
		$('.aton-qrcode').show();
	});
});