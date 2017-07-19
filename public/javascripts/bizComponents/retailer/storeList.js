function initBase(argument) {
    /*取消收藏*/
    $(document).on('click', '.item_cancel', function() {
        var mid = $(this).attr('mid');
        if (!confirm('确定取消收藏该商品吗？')) {
            return;
        }
        Tr.post('/user/favorite/delete', {
        	"id": mid
        }, function(data) {
        	if (data.code != 200) {
        		alert(data.msg);
        		return;
        	}
        	alert('取消成功');
        	window.location.reload();
        });



    });
}

$(function() {
    initBase();
});