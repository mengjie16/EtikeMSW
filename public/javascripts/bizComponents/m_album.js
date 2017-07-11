CDTH5 = {
	albumID: 0,
	albumItemsTemp: null,
	albumItemsCache: null,
	searchvo: {
		// 是否有货查询
		storeFull: false,
		// 价格升序price_a,价格降序price_d,折扣升序dist_a,折扣降序dist_d
		sort: 'price_a'
	},
	itemCates: null
};

function initBase() {
	//默认
	var f = true;
	$(document).on('touchend', '.filter-stock', function(event) {
		event.preventDefault();
		if (f = !f) {
			$(this).find('span').addClass('orange');
			$('.filter-price').data('pricecount',0);
			$('.filter-price').find('i').removeClass('orange');
			$('.filter-price').find('span').removeClass('orange');
			$('.filter-discount').data('discount', 0);
			$('.filter-discount').find('i').removeClass('orange');
			$('.filter-discount').find('span').removeClass('orange');
		} else {
			$(this).find('span').removeClass('orange');
		}
		CDTH5.searchvo.sort = 'price_a';
		loadAlbumItems();
	});

	//点击专辑列表
	$(document).on('touchend', '.J-u-spot', function(event) {
		event.preventDefault();
		$(this).parent().addClass('open').removeClass('close');
		window.lastTopPosition = $('body').scrollTop();
		$('html').css({
			    'overflow-y':'hidden',
			    'position': 'fixed',
			    'top': $('body').scrollTop() * -1
			});

	});
	$(document).on('touchend', '.J-close-btn', function(event) {
		event.preventDefault();
		$(this).parent().parent().removeClass('open').addClass('close');
		$('html').css({
			    'overflow-y':'scroll',
			    'position': 'static',
			    'top': 'auto'
			});
		$('body').scrollTop(window.lastTopPosition);
	});

	//价格处理
	$(document).on('touchend', '.filter-price', function(event) {
		event.preventDefault();
		var count = $(this).data('pricecount');
		$me = $(this);
		$me.next().data('discount', 0);
		$me.next().find('span').removeClass('orange');
		$me.next().find('i').removeClass('orange');
		$('.filter').find('span').removeClass('orange');
		$('.filter').find('i').removeClass('orange');
		if (count == 0) { // 默认价格升序
			$me.data('pricecount', 1);
			$me.find('span').addClass('orange');
			$me.find('.up').addClass('orange');
			f = false;
			$('.filter-stock').find('span').removeClass('orange');
			CDTH5.searchvo.sort = 'price_a';
			loadAlbumItems();
		}
		if (count == 1) { // 价格降序
			$me.data('pricecount', 2);
			$me.find('.down').addClass('orange');
			$me.find('.up').removeClass('orange');
			CDTH5.searchvo.sort = 'price_d';
			loadAlbumItems();
		}
		if (count == 2) { //价格升序
			$me.data('pricecount', 0);
			$me.find('span').removeClass('orange');
			$me.find('i').removeClass('orange');
			CDTH5.searchvo.sort = 'price_a';
			f = true;
			$('.filter-stock').find('span').addClass('orange');
			loadAlbumItems();
		}
	});
	//零售利润
	$(document).on('touchend','.filter-discount',function(event){
		event.preventDefault();
		var count = $(this).data('discount');
		$me = $(this);
		$me.prev().data('pricecount',0);
		$me.prev().find('span').removeClass('orange');
		$me.prev().find('i').removeClass('orange');
		$('.filter').find('span').removeClass('orange');
		$('.filter').find('i').removeClass('orange');
		if(count==0){
			$me.data('discount',1);
			$me.find('span').addClass('orange');
			$me.find('.up').addClass('orange');
			f = false;
			$('.filter-stock').find('span').removeClass('orange');
			CDTH5.searchvo.sort = 'profit_a';
			loadAlbumItems();
		}
		if(count==1){
			$me.data('discount',2);
			$me.find('.down').addClass('orange');
			$me.find('.up').removeClass('orange');
			CDTH5.searchvo.sort = 'profit_d';
			loadAlbumItems();
		}
		if(count==2){
			$me.data('discount',0);
			$me.find('span').removeClass('orange');
			$me.find('i').removeClass('orange');
			CDTH5.searchvo.sort = 'profit_a';
			f = true;
			$('.filter-stock').find('span').addClass('orange');
			loadAlbumItems();
		}
	});

	// 固定导航
	$(window).scroll(function() {
		var nheight = $('#J-brand-filter').height();
		if ($("body").scrollTop() > nheight) {
			$('#J-wrap-filter-bar').removeClass('wrap-filter-bar').addClass('filter-fixed');
		} else {
			$('#J-wrap-filter-bar').addClass('wrap-filter-bar').removeClass('filter-fixed');
		}
	});
}
//localStorage缓存图片
function renderCvs() {
	var lazyloadImage = $('.lazyload');
	if(lazyloadImage.length<1) return;
	var max = lazyloadImage.length;
	for(var i = 0; i < max; i++){
		var imgId = lazyloadImage[i].parentNode.id;
		var imgtime = lazyloadImage[i].parentNode.getAttribute('data-uptime');
		var imageCache = JSON.parse(localStorage.getItem(imgId));
		if(imageCache&&(imageCache.uptime==imgtime||!imageCache.uptime)) {
			lazyloadImage[i].src = imageCache.url;
			continue;
		}else {
			lazyloadImage[i].src = lazyloadImage[i].getAttribute('data-src');
			var img = new Image();
  		img.id = imgId;
  		img.time = imgtime;
  		img.setAttribute('crossOrigin', 'anonymous');
  		img.onload = function(){
  			var _this = this;
  			try{
  				var cvs = document.createElement('canvas');
  				cvs.style.display = 'none';
  				document.body.appendChild(cvs);
  				var rcvs = cvs.getContext('2d');
  				cvs.width = this.width;
  				cvs.height = this.height;
  				rcvs.drawImage(this,0,0,this.width,this.height);
  				setTimeout(function(){
  					var data = {};
  					data.uptime = _this.time;
  					data.url = cvs.toDataURL("image/png");
  					localStorage.setItem(_this.id,JSON.stringify(data));
  					document.body.removeChild(cvs);
  				},200);
  			}catch(ex){}
  		}
  		img.src = lazyloadImage[i].getAttribute('data-src');
		}
	}
}
$(function() {
	CDTH5.albumItemsTemp = $('#albumItemsTemp').remove().text();
	initBase();
	// 加载专辑商品列表
	loadAlbumItems();
	var aheight = document.documentElement.clientHeight;
	$('.u-body-menus-wrap ul').css('height',(aheight-140)+'px');
});

// 加载专辑商品列表
function loadAlbumItemsHTML() {
	var items = {
		'albumItems': !CDTH5.albumItemsCache ? new Array() : CDTH5.albumItemsCache
	};
	var output = Mustache.render(CDTH5.albumItemsTemp, $.extend(items, {
		'profitStr':function(){
			if(this.profit>=0){
				return (this.profit*100).toFixed(0)+"%";
			}
			return 0+"%";
		},
		'priceStr':function(){
			
			return this.price/100;
		}
	}));
	$('#album_item_list').empty().append(output);
	renderCvs();
}

// 加载专辑商品列表
function loadAlbumItems() {
	// 默认查询条件:所有商品，价格升序
	var search = $.extend({
		id: CDTH5.albumID
	}, CDTH5.searchvo);
	Tr.get('/h5/album/item/query', search, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDTH5.albumItemsCache = data.results;
		loadAlbumItemsHTML();
	});
}

// 输出宝贝类目HTML
function loadItemCatesHTML() {
	$.each(CDTH5.itemCates, function(index, cate) {

	})
}

function itemCateChildHTML() {

}

// 加载宝贝类目信息
function loadItemCates() {
	if (CDTH5.itemCates && CDTH5.itemCates.length > 0) {
		loadItemCatesHTML();
	}
	Tr.get('/dpl/cate/level/list', {
		'level': 1
	}, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDTH5.itemCates = data.results;
		loadItemCatesHTML();
	});
}