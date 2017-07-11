CDT = {
	createTemp:"<tr mid='-999'><td class='thcellId'><div class='tdcell'></div></td><td><div class='tdcell'><input type='text' class='inputText cate_name_item' data-ctype='bottom'></div></td><td><div class='tdcell'><i class='iconfont mr10 moveUpAll'>&#xe604;</i><i class='iconfont mr10 moveUp'>&#xe600;</i><i class='iconfont mr10 moveDown'>&#xe60d;</i><i class='iconfont mr10 moveDownAll'>&#xe601;</i></div></td><td><div class='tdcell'></div></td><td><div class='tdcell'style='text-align: right;'><a href='javascript:;' class='btnStd btnSmall delbtn'>删除</a></div></td></tr>",
	token: null,
	uptoken: null,
	uploadImgCache: new Array(),
	cateTemp: null,
	loadedCateCache: {},
	currPageNo: 1,
	pageSize: 10,
	cateId:0,
	isChange:false
};
// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore:""
});
function initBase(argument) {
	//置顶
	$(document).on('click','.moveUpAll',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var index = $tr.index();
		if(index!=0){
			$tr.prependTo('#cateContainer table tbody');
			//$me.css('color','#ccc');
		}
		// 标记为置顶
		var $input = $tr.find('.cate_name_item');
		$input.data({'ctype':'top'});
	});
	//置底
	$(document).on('click','.moveDownAll',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var index = $tr.index();
		if(index!=($('.container>tr').length - 1)){
			$tr.appendTo('#cateContainer table tbody');
			//$(this).css('color','#ccc');
		}
		// 标记为置底
		var $input = $tr.find('.cate_name_item');
		$input.data({'ctype':'bottom'});
	});

	//向上移动
	$(document).on('click','.moveUp',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var $prev = $tr.prev();
		var index = $tr.index();
		// 上一个元素(存在)
		if($prev){
			var $input = $prev.find('.cate_name_item');
			var $currentInput = $tr.find('.cate_name_item');
			$currentInput.data('n_cid',$input.data('cid'));
			// 上一个元素是置顶，当前向上move也应该是置顶
			if($input.data('ctype')){
				$currentInput.data('ctype',$input.data('ctype'));
			}else{
				$currentInput.data('ctype','');
			}
			$prev.before($tr);
		}
	});
	//向下移动
	$(document).on('click','.moveDown',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var $next = $tr.next();
		var index = $tr.index();
		if($next){ // 下一个元素(存在)
			var $input = $next.find('.cate_name_item');
			var $currentInput = $tr.find('.cate_name_item');
			// 上一个元素关联当前元素
			$input.data('n_cid',$currentInput.data('cid'));
			// 上一个元素是置顶，当前向上move也应该是置顶
			if($input.data('ctype')){
				$currentInput.data('ctype',$input.data('ctype'));
			}else{
				$currentInput.data('ctype','');
			}
			$next.after($tr);
		}
	});
	
	// 关键字查询品牌
	$(document).on('click', '#cateSearchBtn', function() {
		loadCateList(1);
	});
	$(document).on('keypress','#titleName',function(e){
		//var ev = document.all ? window.event : e;
		e.preventDefault();
		e.stopPropagation();
		if (e.keyCode == 13) {
			loadCateList(1);
		}
	});
	// 类目信息保存
	$(document).on('click','#saveBtn',function(){
		if(!CDT.isChange){
			alert("您还未进行任何操作!");
			return;
		}
		var $cateTr = $('#cateContainer table tbody tr');
		var postArr = new Array();
		var postObj = {
			'parentId':CDT.cateId
		};
		//
		$.each($cateTr,function(index,obj){
			var $input = $(obj).find('.cate_name_item');
			var cateObj = {
				id: !$input.data('cid')?-999:$input.data('cid'),
				ctype:!$input.data('ctype')?"":$input.data('ctype'),
				name:$input.val()
			}
			postObj['list['+index+'].id'] = !$input.data('cid')?-999:$input.data('cid');
			postObj['list['+index+'].changeType'] = !$input.data('ctype')?"":$input.data('ctype');
			postObj['list['+index+'].name'] = $input.val();
		});
		//return;
		Tr.post('/sys/cate/modify',postObj, function(data) {
			if (data.code != 200){
				alert(data.msg);
				return;
			}
			loadCateList(CDT.currPageNo);
			alert('保存成功!');
		},{loadingMask: false});
	})

	// 添加类目
	$(document).on('click','#cateCreateBtn',function(){
		$('#cateContainer table tbody').append(CDT.createTemp);
	})

	// 是否进行过操作标识
	$(document).on('change','#cateContainer .cate_name_item',function(){
		CDT.isChange  = true;
	});

	// 删除类目
	$(document).on('click','.delbtn',function(){
		var $tr = $(this).parents('tr').eq(0);
		if (!confirm('确定删除该类目吗？')) {
  		return;
		}
		var id = $tr.attr('mid');
		if(!id) return;
		if(id!=-999&&id>0){
			Tr.post('/sys/cate/delone',{'id':id}, function(data) {
				if (data.code != 200){
					alert('删除失败！');
					return;
				}
				$tr.remove();
				alert('删除成功!');
				loadCateList(CDT.currPageNo);
			},{loadingMask: false});
		}else{
			$tr.remove();
		}

	})
}
//加载列表
function loadCateList(pageNo) {
	var cvo = {
		'cvo.pageSize': CDT.pageSize,
		'cvo.pageNo': pageNo,
		'cvo.name':$('#titleName').val()?$('#titleName').val():''
	};
	if(CDT.cateId>0){
		$.extend(cvo,{'cvo.id':CDT.cateId,'cvo.querySub':true});
	}else{
		$.extend(cvo,{'cvo.level':1,'cvo.queryLevel':true});
	}
	Tr.get('/dpl/cate/list',cvo, function(data) {
		if (data.code != 200) return;
		// 保存到缓存
		CDT.loadedCateCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.cateTemp, $.extend(data, {
			formatTimer: function() {
				if (this.createTime) {
					return new Date(this.createTime).Format('yyyy-MM-dd');
				}
				return '----';
			}
		}));
		if(!output){
			$('#cateContainer table tbody').html('');
			$('.no_more_data').show();
			$('.pagination').hide();
			return;
		}else{
			$('.no_more_data').hide();
			$('.pagination').show();
		}
		$('#cateContainer table tbody').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadCateCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}
function loadCateCallBack(index, jq) {
	loadCateList(index + 1);
}
// 初始化数据
$(function() {
	CDT.cateTemp = $('#cateTemp').remove().val();
	CDT.cateId = $('#param_cid')&&$('#param_cid').val()?parseInt($('#param_cid').val()):-9999;
	initBase();
	Mustache.parse(CDT.cateTemp);
	loadCateList(1);
});