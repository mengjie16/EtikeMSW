CDT = {
	freightManageTemp: null,
	loadedFreightManageCache: {},
	currPageNo: 1,
	pageSize: 10
};
function initBase(argument) {
	// 关键字查询模板
	$(document).on('click', '#SearchBtn', function() {
		loadFreightManageList(1);
	});

	// 去除enter默认事件
	$(document).on('keypress','input[type="text"]',function(e){
		if (e.keyCode == 13) {
			e.preventDefault();
		}
	});
}

$(function() {
	CDT.freightManageTemp = $('#freightManageTemp').remove().val();
	Mustache.parse(CDT.freightManageTemp);
	initBase();
	loadFreightManageList(1);
});

// 加载模板列表
function loadFreightManageList(pageNo) {
	Tr.get('/sys/freight/query', {
		'vo.pageSize': CDT.pageSize,
		'vo.pageNo': pageNo,
		'vo.aboutBrand':$('#brandName').val()?$('#brandName').val():'',
		'vo.aboutSupplier':$('#supName').val()?$('#supName').val():''
	}, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedFreightManageCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.freightManageTemp, $.extend(data, {
			createTimeStr: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd');
			},
			files: function() {
				if (!this.freights.length) {
					return 0;
				} else {
					return this.freights.length;
				}
			},
			tempTypeTran: function(){
				if(this.tempType=="static"){
					return "静态"
				}else{
					return "动态"
				}
			}
		}));
		$('#freightContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#freightContainer .pagination').hide();
			return;
		}
		$('#freightContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadFreightManageCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

function loadFreightManageCallBack(index, jq) {
	loadFreightManageList(index+1);
}
