// 数据定义
CART = {
		cartCache: new Array(),
		loadCart:false
	};
	// 初始化内容
$(function() {
	// 加载购物车
	loadCartData();
	// 显示购物车内容
	$(document).on('mouseenter','.cartblock',function() {
		loadCartData();
		$('.nav-content').show();

	});
	// 鼠标移开购物车
	$(document).on('mouseleave','.cartblock',function() {
		$('.nav-content').hide();
	});

	//删除购物车里的条目
	$(document).on('click','.delete',function(){
		var id =$(this).data('id');
		if(!id){
			return;
		}
		Tr.post('/retailer/cart/remove',{
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'itemId': id
		},function(data){
			if (data.code != 200) return;
			CART.cartCache = new Array();
			loadCartData();
		}, {
			loadingMask: false
		});
	});
});

// 加载购物车数据
function loadCartData(){
	if(CART.loadCart!=true) return;
	if (CART.cartCache.length <= 0) {
			Tr.get('/retailer/cart/query', {},
				function(data) {
					if (data.code == 200) {
						CART.cartCache = data.results;
					};
					printCartHtml();
				},{loadingMask:false});
		} else {
			printCartHtml();
		}
}
// 输出购物车内容
function printCartHtml() {
	if(CART.cartCache.length>0){
		$('.header_util-right .emptytxt').hide();
		$('.cartContainer,.bottom_cart_fun').show();
		$('#cartContainer').empty();
		var totalPrice = 0,
			  totalCount = 0;
		$.each(CART.cartCache, function(index, obj) {
			var $titledt = $("<dt><a target='_blank' href='/item/" + obj.id + "'><img src='" + obj.sku.img + "' style='width:36px;'></a></dt>");
			var $basedd = $("<dd class='title'><a target='_blank' href='/item/" + obj.id + "' class='titleName'>"+obj.title+"</a></dd><dd class='price'>¥" + obj.cartPrice + "元&nbsp;×&nbsp;"+obj.cartCount+"</dd>");
			var $skudd = $("<dd class='specInfos'><span title = '颜色：" + obj.sku.color + "' class = 'specItem' > 颜色:" + obj.sku.color + "</span></dd>");
			var $fundd = $("<dd class = 'action'><a data-id='"+obj.id+"' class = 'delete' title = '删除' href = '#' >删除</a></dd>");	
			var $itemdl = $("<dl></dl>");
			$itemdl.append($titledt);
			$itemdl.append($basedd);
			$itemdl.append($skudd);
			$itemdl.append($fundd);
			totalPrice += parseInt(obj.cartPrice)*parseInt(obj.cartCount);
			totalCount += parseInt(obj.cartCount);
			if ($itemdl) {
				$('#cartContainer').append($itemdl);
			}
		});
		$('#totalPrice').text("¥"+totalPrice);
		$('.cartNum em').text(totalCount);
		return;
	}
	$('.header_util-right .emptytxt').show();
	$('.cartContainer,.bottom_cart_fun').hide();
}