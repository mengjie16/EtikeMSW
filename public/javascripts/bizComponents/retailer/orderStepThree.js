function initBase() {
	//选择匹配的规格
	$(document).on('click','.selected',function(){
		$(this).parent().find('.selList').show();
	});
	//图片展示
	$(document).on('hover','.selList li',function(){
		$(this).parents('.sel_match').find('.imgcontainer img').attr('src',$(this).data('src'));
		$(this).parents('.sel_match').find('.imgcontainer').toggle();
	});

	//选择规格
	$(document).on('click','.selList li',function(){
		var item = $(this).text();
		$(this).parents('.sel_match').find('.selected').val(item);
		$(this).parent().parent().hide();
		$(this).parents('.product_sku').find('.pleaseMatch').hide();
		// 检查当前商品规格是否匹配完成，完成显示匹配完成按钮
		var $sku_items = $(this).parents('.sku_items').find('.sku_item');
		if(!$sku_items){
			return;
		}
		var selectCount = 0;
		$.each($sku_items,function(index,skuItem){ 
			var $selectedSku = $(skuItem).find('.sel_match input[name=selected]');
			if($selectedSku&&$selectedSku.val()!=''){
				selectCount ++;
			}
		});
		if(selectCount==$sku_items.length){
			var $order_product = $(this).parents('.order_product');
			if($order_product.data('edit_item')==true){
				return;
			}
			var $matchBtn = $order_product.find('.match_done p');
			if($matchBtn){
				$matchBtn.show();
			}
		}
	});
	//校验输入id
	$(document).on('keyup','input[name=itemId]',function(){
		var reg = /^[1-9]\d*$/;
		var $me = $(this),
				id = $me.val();
		if(!reg.test(id)){
			$me.val('');
		}else{
			$me.val(id);
		}	
	});
	//读取商品id
	$(document).on('click','.readbtn',function(){
		var $me = $(this),
			$orderProduct = $(this).parents('.order_product');
			$match = $me.parents('.product_base_info').next();

		if($me.text()=='匹配'){
			var itemId = $me.prev().val();
			if (!itemId && itemId == '') return;
			var url = '/dpl/item/query';
			Tr.get(url, {
				"id": itemId
			}, function(data) {
				if (data.code != 200 || !data.results) {
					alert('商品不存在');
					$me.prev().val('');
					return;
				}
				if(!data.results.freightTemp){
					alert('该商品未完善运费信息，无法计算运费');
					$me.prev().val('');
					return;
				}
				var $matchBtn = $me.parents('.order_product').find('.match_done p');
				if($matchBtn){
					$matchBtn.hide();
				}
				$me.parents('.matchSkuList').find('.selected').val('');
				$match.show();
				$me.text('编辑');
				$me.parents('.order_product').find('.pleaseMatch').show();
				$me.parents('.order_product').find('.selected').val('');
				$me.prev().attr('readonly',true);
				$me.parents('.product_base_info').find('.status').text('正在匹配').css('color','#ffa200');
				$orderProduct.data('edit_item',false);
				var $choosedHtml = '';
				$.each(data.results.skus, function(index, obj) {
					$choosedHtml += '<li class="provenceItem" data-src="'+obj.img+'">' + obj.color + '</li>';
				});
				$('.selList ul').html($choosedHtml);
			});
		}else{
			// 重新编辑不能点击完成匹配
			$orderProduct.data('edit_item',true);
			$orderProduct.find('.match_done_btn').hide();
			$me.prev().removeAttr('readonly');
			$me.text('匹配');
		}
	});

	//点击匹配完成
	$(document).on('click','.match_done_btn',function(){
		// 匹配到的商品规格信息展示,并且于原有规格对应
		var $sku_items = $(this).parents('.product_sku_info').find('.sku_item');
		var $matchHtml = '';
		// 当前容器
		var $productSkuInfo = $(this).parents('.product_sku_info');
		var $productMatched = $productSkuInfo.next();
		var $excelList = $productMatched.find('.excel_list ul li');
		$.each($sku_items,function(index,skuItem){
			var $sku = $(skuItem).find('input.selected').val();
			if($sku){
				$matchHtml += '<li class="provenceItem">' + $sku + '</li>';
				$excelList.eq(index).data('match-sku',$sku);
			}
		});
		$matchHtml = '<ul>'+$matchHtml+'</ul';
		$productMatched.find('.match_list').html($matchHtml);
		$productMatched.show();
		// 隐藏匹配基本信息容器, 并且展示商品ID信息
		var $prodcutBaseInfo = $productSkuInfo.prev();
		var itemId = $prodcutBaseInfo.find('input[name=itemId]').val();
		$productMatched.find('.product_id span').text(itemId);
		$productMatched.find('input[name=product_itemId]').val(itemId);
		$prodcutBaseInfo.hide();
		// 隐藏规格匹配信息容器
		$productSkuInfo.hide();
		// 编辑当前商品为匹配完成
		var $orderProduct = $(this).parents('.order_product');
		$orderProduct.attr('matched',true);
		if(matchStatus()){
			$('#btnSubmit').data('next',true);
		}else{
			$('#btnSubmit').data('next',false);
		}
	});
	$('body').on('click',function(e) {
		if (!$(e.srcElement).is('.sel_match *') || !$(e.target).is('.sel_match *') || !e.srcElement) {
			$('.selList').hide();	
		}
	});
	//重新匹配
	$(document).on('click','.rematch',function(){
		$(this).parents('.order_product').find('input[name=selected]').val('');
		$(this).parents('.order_product').find('.match_done p').hide();
		$('.selList ul').html('');
		var $orderProduct = $(this).parents('.order_product');
		$orderProduct.attr('matched',false);
		$orderProduct.find('.product_base_info').show();
		$orderProduct.find('.product_sku_info').show().next().hide();
		if(matchStatus()){
			$('#btnSubmit').data('next',true);
		}else{
			$('#btnSubmit').data('next',false);
		}
	});
	$('#btnSubmit').data('next',false);
	//下一步
	$(document).on('click','#btnSubmit',function(){
		var $me = $(this);
		if(!matchStatus()){
			alert('商品未全部匹配！');
			$me.data('next',false);
			return;
		}
		var $matchedSku = $('.product_matched_content .excel_list li');
		if(!$matchedSku){
			alert('商品匹配过程出错！');
			return;
		}
		if ($me.data('next') == false) {
			return;
		}
		var matchSkuParam = {};
		$.each($matchedSku,function(index,skuLi){
			var key = $(skuLi).data('key');
			var mSku = $(skuLi).data('match-sku');
			if(key&&mSku){
				matchSkuParam['sku['+key+']'] = mSku;
			}
		});
		var $matchedProducts = $('#product_list .product_matched_content');
		$.each($matchedProducts,function(index,product){
			var key = $(product).find('input[name=product_key]').val();
			var itemId = $(product).find('input[name=product_itemId]').val();
			if(key&&itemId){
				matchSkuParam['productName['+key+']'] = itemId;
			}
		});
		// 提交excel_sku总匹配结果
		Tr.post('/retailer/ordervo/generate',matchSkuParam, function(data) {
			if (data.code == 200) {
				window.location.href = '/retailer/order/stepFour';
			}else{
				alert(data.msg);
			}
		});
		
		
	});
}
function matchStatus(){
	// 检查是否全部完成匹配
	var $productList = $('#product_list .order_product');
	// 查找所有已匹配项
	var $matchedList = $('#product_list .order_product[matched=true]');
	if($matchedList.length<$productList.length){
		return false;
	}else if($matchedList.length == $productList.length){
		return true;
	}
}
$(function(){
	initBase();
});