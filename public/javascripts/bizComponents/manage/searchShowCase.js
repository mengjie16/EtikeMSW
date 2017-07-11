CDT = {
	isChange:false
}
function initBase() {
	// 是否进行过操作标识
	$(document).on('change','#showContainer .case_id_item',function(){
		var reg = /^[0-9]+$/;
		var $me = $(this),
				id = $me.val();
		if(!reg.test(id)){
			$me.val('');
			return;
		}
		CDT.isChange  = true;
	});
	//置顶
	$(document).on('click','.moveUpAll',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var index = $tr.index();
		if(index!=0){
			$tr.prependTo('#showContainer table tbody');
		}
		// 标记为置顶
		var $input = $tr.find('.case_id_item');
		$input.data({'ctype':'top'});
	});
	//置底
	$(document).on('click','.moveDownAll',function(){
		CDT.isChange  = true;
		var $me = $(this);
		var $tr = $me.parents('tr').eq(0);
		var index = $tr.index();
		if(index!=($('.container>tr').length - 1)){
			$tr.appendTo('#showContainer table tbody');
		}
		// 标记为置底
		var $input = $tr.find('.case_id_item');
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
			var $input = $prev.find('.case_id_item');
			var $currentInput = $tr.find('.case_id_item');
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
			var $input = $next.find('.case_id_item');
			var $currentInput = $tr.find('.case_id_item');
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
	// 明星商品信息保存
	$(document).on('click','#saveBtn',function(){
		if(!CDT.isChange){
			alert("您还未进行任何操作!");
			return;
		}
		var len = $('#showContainer .case_id_item').length;
		var $caseTr = $('#showContainer table tbody tr');
		var postArr = new Array();
		var postObj = {
			'authenticityToken':$('input[name=authenticityToken]').val()
		};
		//
		var container = [];
		$.each($caseTr,function(index,obj){
			var $input = $(obj).find('.case_id_item');
			if($input.val()){
				container.push($input.val());
			}
		});
		if(container.length<len){
			alert('商品未配置完全');
			return;
		}
		postObj['setting._id'] = $('#setting_id').val();
		postObj['setting.itemIds'] = container.join(',');
		Tr.post('/sys/item/starsave',postObj, function(data) {
			if (data.code != 200){
				alert(data.msg);
				return;
			}
			alert('保存成功!');
		});
	});
	//点击跳转至相应详情页
	$(document).on('click','.jumping',function(){
		var id = $(this).parents('tr').find('.case_id_item').val();
		window.open('/item/'+id);
	});
}
$(function() {
	initBase();
});