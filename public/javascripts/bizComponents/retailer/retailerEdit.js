//零售商的表单验证
function userValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'user.name': {
				required: true,
				username: true
			},
			'user.email': {
				required: true,
				email: true
			}
		},
		success: function(label, element) {}
	};
	return options;
}
$(function(){
// 用户信息保存
	$(document).on('click', '#btnUserSubmit .btnStd', function() {
		var validator = $('#frmEditUser').validate(userValidateOpts());
		if (validator.form()) {
			Tr.post('/user/info/edit',{
				'name': $("input[name='user.name']").val(),
				'email': $("input[name='user.email']").val()
			}, function(data) {
				if (data.code != 200) {
					alert('保存失败');
					return;
				}
				alert('保存成功');
				window.location.reload();
			},{ 
				loadingMask: false
			});
		}
	});

});