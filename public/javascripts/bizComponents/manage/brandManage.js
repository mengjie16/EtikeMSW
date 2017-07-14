CDT = {
	token: null,
	uptoken: null,
	uploadImgCache: new Array(),
	brandTemp: null,
	loadedBrandCache: {},
	currPageNo: 1,
	pageSize: 10
};
// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore:""
});
$.extend($.validator.messages, {
	titlename: Tr.error('请输入正确格式'),
	normalname: Tr.error('请输入正确格式')
});
$.validator.methods.titlename = function(value, element, param) {
	return /^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$/.test(value);
};
$.validator.methods.normalname = function(value, element, param) {
	return /^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$/.test(value);
};
//编辑的表单验证
function ValidateOpts() {
	// 校验规则
	var options = {
		onkeyup: true,
		rules: {
			'brand.name': {
				required: true,
				maxlength: 30,
				minlength: 2,
				titlename: true
			},
			'brand.secondaryName': {
				//required: true,
				maxlength: 30,
				//minlength: 2,
				//titlename: true
			},
			'brand.picUrl': {
				required: true
			}
		},
		success: function(label, element) {}
	}
	return options;
}
function initBase(argument) {
	// 添加品牌弹框
	$(document).on('click', '#brandCreateBtn', function() {
		$('#txtBrandId').val(0);
		$('#texdiscribe').val('');
		$('#frmEditBrand').find("input[type!='hidden']").val('');
		$('#frmEditBrand #T_adBanner img').removeAttr('src');
		$('#picClickDesc').text('点击添加图片');
		Tr.popup('editBrand');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//修改品牌
	$(document).on('click', '.editbtn', function() {
		var mid = $(this).parents('tr').attr('mid'),
			model = $('#editBrand').data('model');
		$.each(CDT.loadedBrandCache, function(i, n) {
			if (n.id == mid) {
				$('#txtBrandId').val(mid);
				$('#txtTitle').val(n.name);
				$('#txtSecondTitle').val(n.secondaryName);
				if(n.picUrl){
					$('#hidPicUrl').val(n.picUrl);
					$('#T_adBanner img').attr('src', n.picUrl);
				}
				$('#txtCompany').val(n.company);
				$('#txtHolder').val(n.holder);
				$('#texdiscribe').val(n.descption);	
			}
		});
		$('#picClickDesc').text('点击修改图片');
		Tr.popup('editBrand');
	});

	// 关键字查询品牌
	$(document).on('click', '#brandSearchBtn', function() {
		loadBrandList(1);
	});

	// 品牌查询enter
	$(document).on('keypress','#brandName',function(e){
		e.stopPropagation();
		if (e.keyCode == 13) {
			loadBrandList(1);
		}
	});

	/*删除行*/
	$(document).on('click', '.delbtn', function() {
		var mid = $(this).parents('tr').attr('mid');
		if (!confirm('确定删除该品牌吗？')) {
			return;
		}
		Tr.get('/sys/brand/delone', {
			"id": mid
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功');
			loadBrandList(CDT.currPageNo);
		},{loadingMask: false});
	});

	// 确认保存
	$(document).on('click', '#btnBrandfirm', function() {
		var validator = $('#frmEditBrand').validate(ValidateOpts());
		var $me = $(this);
		if (!validator.form()) {
			return;
		}
		var param = {
			'brand.id': $('input[name="brand.id"]').val(),
			'brand.name': $('input[name="brand.name"]').val(),
			'brand.secondaryName': $('input[name="brand.secondaryName"]').val(),
			'brand.picUrl': $('input[name="brand.picUrl"]').val(),
			'brand.company': $('input[name="brand.company"]').val(),
			'brand.holder': $('input[name="brand.holder"]').val(),
			'brand.descption': $('textarea[name="brand.descption"]').val()
		}
		Tr.post('/sys/brand/save', param, function(data) {
			if (data.code != 200){
				alert('更新失败!');
				return;
			}
			alert('更新成功！');
			$me.parents('.popWrapper').hide();
			$('html').removeClass('overflow-hidden');
			loadBrandList(CDT.currPageNo);
		});
	});
}

$(function() {
	CDT.brandTemp = $('#brandTemp').remove().val();
	Mustache.parse(CDT.brandTemp);
	initBase();
	loadBrandList(1);
	loadUpToken();
});

// 加载品牌列表
function loadBrandList(pageNo) {
	$('#choolseAll').prop('checked',false);
	Tr.get('/dpl/brand/list', {
		'bvo.pageSize': CDT.pageSize,
		'bvo.pageNo': pageNo,
		'bvo.name':$('#brandName').val()?$('#brandName').val():''
	}, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedBrandCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.brandTemp, $.extend(data, {}));
		$('#brandContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#brandContainer .pagination').hide();
			return;
		}
		$('#brandContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadBrandCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

function loadBrandCallBack(index, jq) {
	loadBrandList(index+1);
}

function loadUpToken() {
	// 先获取token
	Tr.get('/dpl/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		initItemMainPicUploader();
	});
}

function initItemMainPicUploader() {
	var option = Tr.uploadOption();
	option.domain = APP.QnDomain + '.eitak.com/';
	option.max_file_size = '500kb';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'T_adBanner';
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
			$('#T_adBanner img').attr('src', sourceLink);
			CDT.uploadImgCache[file.name] = sourceLink;
			$("#T_adBanner #hidPicUrl").val(sourceLink);
			$('#frmEditBrand').valid();

		},
		'Error': function(up, err, errTip) {
			if (err.code == -601) {
				alert('文件后缀错误');
				return;
			}
			if (err.code == -602) {
				var filename = err.file.getSource().name,
					sourceLink = CDT.uploadImgCache[filename];
				// // alert('请不要上传重复的图片');
				$('#T_adBanner img').attr('src', sourceLink);
				$("#T_adBanner  #hidPicUrl").val(sourceLink);
				return;
			}
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
}