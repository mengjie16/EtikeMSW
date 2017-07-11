function initBase() {
	
}
//nav加载
function loadNav(){
	var txt = $('#boriboriCSMenus').find('.active').text(),
			tp = $('#boriboriCSMenus').find('.active').parent().prev().text();
	$('#mdetail').text('> '+txt);
	$('#mblock').text('> '+tp);
}
$(function(){
	initBase();
	loadNav();
})