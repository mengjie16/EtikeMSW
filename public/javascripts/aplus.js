(function(a, b) {
	function adjustFrameStyle(b) {
		//进行dom操作
		var doc =  window.document,docEl = doc.documentElement;
		var $body = doc.getElementsByTagName('body')[0],$html = doc.getElementsByTagName('html')[0];
		var width = docEl.getBoundingClientRect().width;
		if(width > 640){
			width = 640;
		}else if(width<320){
			width=320;
		}
		var rem = width / 10;
		docEl.style.fontSize = rem + 'px';
	}
	var tid;
	a.addEventListener('resize', function() {
        clearTimeout(tid);
        tid = setTimeout(adjustFrameStyle, 300);
    }, false);
    a.addEventListener('pageshow', function(e) {
        if (e.persisted) {
            clearTimeout(tid);
            tid = setTimeout(adjustFrameStyle, 300);
        }
    }, false);

	b.adjustFrameStyle = adjustFrameStyle;

})(window, window.PLib || (window.PLib = {}));

PLib.adjustFrameStyle();