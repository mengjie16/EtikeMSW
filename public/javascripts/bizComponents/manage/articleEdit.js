CDT = {
	token: null,
	uptoken: null,
	ue: null
};
//发布商品的表单验证
function ValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			'article.title': {
				required: true,
				maxwords: 15,
				minwords: 2
			},
			'article.createTime': {
				required: true
			},
			'article.content':{
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}

function initBase(argument) {
	var currentTime = new Date().Format("yyyy-MM-dd");
	$('.inputDate').datePicker({
		startDate: '2015-01-01',
		endDate: currentTime,
		clickInput: true,
		verticalOffset: 30,
		horizontalOffset: -26
	});

	// 提交保存
	$(document).on('click', '#btnSubmit', function() {
		// 处理文章内容
		$('#articleContent').val($('#ueditor_textarea_editorValue').val());
		// 处理文章类型
		var $articleType =  $("input[name='articleType']:checked");
		$('#articleTid').val($articleType.val());
		$('#articleTname').val($articleType.data('text'));
		var validator = $('#formartical').validate(ValidateOpts());
		// 提交
		if (validator.form()) {
			$('#formartical').submit();
		}
	});	
}

$(function() {
	// 初始化uditor
	CDT.ue = UE.getEditor('contentEditor', {
		initialFrameHeight: 300,
		autoWidth: false
	});
	CDT.ue.addListener('ready', function(editor) {
		$('#editorControl').css('display', 'table-cell');
		$('#edui147_state').unbind();
		$('#edui147_body').unbind().find('.edui-box').html('');
		loadUpToken_u();
	});
	// 初始化数据
	initBase();

	/*验证*/
	$('#btnSubmit').click(function() {
		$('#articleDetail').val($('#ueditor_textarea_editorValue').val());
		var validator = $('#formartical').validate(ValidateOpts());
		// 提交
		if (validator.form()) {
			$('#formartical').submit();
		}
	});
});

function loadUpToken_u() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		var option = Tr.uploadOption();
		option.domain = APP.QnDomain + '.eitak.com/';
		option.uptoken = CDT.uptoken;
		option.browse_button = 'edui147_body';
		option.init = {
			'BeforeUpload': function(up, file) {
				// 检查文件类型

			},
			'UploadProgress': function(up, file) {
				// 每个文件上传时,处理相关的事情，转菊花、显示进度等
			},
			'FileUploaded': function(up, file, info) {
				var domain = up.getOption('domain');
				var res = $.parseJSON(info);
				var sourceLink = domain + res.key;
				CDT.ue.execCommand('inserthtml', '<br/><img src="' + sourceLink + '"/>');

			},
			'Error': function(up, err, errTip) {
				//上传出错时,处理相关的事情
				alert('上传失败！');
			},
			'Key': function(up, file) {
				// 若想在前端对每个文件的key进行个性化处理，可以配置该函数
				// 该配置必须要在 unique_names: false , save_key: false 时才生效
				var key = "";
				// do something with key here
				return key
			}
		}
		Qiniu.uploader(option);
	}, {
		loadingMask: false
	});
}