CDTH5 = {
	h5activityId: null,
	h5activityCache:{},
	groupitemsTemp: null
};

// 初始化数据
$(function() {
	CDTH5.groupitemsTemp = $('#groupitemsTemp').remove().html();
	// 加载活动信息
	loadActivityContent();
});

function loadActivityTitle() {
	$('#h5activityTitle').text(CDTH5.h5activityCache.title);
}

// 加载主活动商品列表
function loadActivityItems() {
	var items = {
		'groupItemVos': !CDTH5.h5activityCache.groupItemVos ? new Array() : CDTH5.h5activityCache.groupItemVos
	};
	if (items.groupItemVos.length <= 0) {
		items.groupItemVos.push(new Array());
	};
	var output = Mustache.render(CDTH5.groupitemsTemp, $.extend(items, {
		groupItem: function() {
			return this;
		}
	}));
	$('#itemContainer').append(output);
}

// 加载h5活动信息
function loadActivityContent() {
	Tr.get('/sys/h5/activity/query', {
		'id': CDTH5.h5activityId
	}, function(data) {
		if (data.code != 200 || !data.results || data.results.length <= 0) return;
		// 保存到缓存
		CDTH5.h5activityCache = data.results;
		loadActivityTitle();
		loadActivityItems();
	}, {
			loadingMask: false
		});
}


