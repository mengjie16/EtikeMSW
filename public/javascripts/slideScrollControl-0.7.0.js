//slideScrollConntrol
//ver.0.5 20120415 kkh 은마군
//ver.0.6 20120601 kkh 은마군
//ver.0.61 20120807 kkh 은마군
//ver.0.71 20120907 kkh 은마군
//tinyscrollbar 보고 많이 배꼈음

jQuery.slide = jQuery.slide || {};

jQuery.slide.scrollControl = {
    options: {
        //axis: 'y', // vertical or horizontal slide? ( x || y ).
        contentWidth: 5,  //how to move at once? (Integer pixel)
        duration: 1000, //animation duration (Integer millisecond);      
		minCount: 4,
		isRotate:true,
        reverse: false, //moving direction reverse
		autoRolling : false,
        timerInterval : 3000	
    }
};

jQuery.fn.slidescrollcontrol = function (options) {
    var options = jQuery.extend({}, jQuery.slide.scrollControl.options, options);
    this.each(function () { jQuery(this).data('', new slideControl(jQuery(this), options)); });
    return this;
};

function slideControl(root, options) {
    var oSelf = this;
    var oWrapper = root;
    var sReverse = options.reverse;
    var oPrev = { obj: jQuery(sReverse ? ".next" : ".prev", root) };
    var oNext = { obj: jQuery(sReverse ? ".prev" : ".next", root) };
    var oSlideScrollContents = { obj: jQuery(".slideScrollContents", root) };
    var oSlideScrollContent = { obj: jQuery(".slideScrollContent", root) };
    var sContentWidth = options.contentWidth;
    var sDuration = options.duration;
	var sMinCount = options.minCount;
	var sIsRotate = options.isRotate;
    var prevClicked = 0;
    var nextClicked = 0;
	var oSlideScrollContent_length = jQuery(oSlideScrollContent.obj).length;
	var sTimer = null;
	var sAutoRolling = options.autoRolling;
	var sTimerInterval = options.timerInterval;

    function init() {
        setEvent();
		if(oSlideScrollContent_length > sMinCount)
		{
			jQuery(oSlideScrollContents.obj[0]).prepend(jQuery(oSlideScrollContent.obj[oSlideScrollContent.obj.length -1]));
		}
		else
		{
			jQuery(oSlideScrollContents.obj[0]).css("left", "0px");			
		}

		if(sAutoRolling == true)
		{
			sTimer = setTimeout(function() { timer(); }, sTimerInterval);
		}
    }

    function setEvent() {
		for (var i = 0; i <=  oSlideScrollContent_length; i++ ){
			jQuery(oSlideScrollContent.obj[i]).attr("value",i + 1);
		}

        jQuery(oSlideScrollContents.obj[0]).css("width", ( sContentWidth * oSlideScrollContent_length));        

        jQuery(oPrev.obj[0]).click(function () {
            if (prevClicked == 1 || oSlideScrollContent_length <= sMinCount) {
                return;
            }
			else if(!sIsRotate){
				if(jQuery(oSlideScrollContents.obj[0]).children("li").eq(sMinCount).attr("value") == oSlideScrollContent_length){
					return;
				}
			}

			var html = jQuery(oSlideScrollContents.obj[0]).children("li").eq(0).html();
			var targetLiValue = jQuery(oSlideScrollContents.obj[0]).children("li").eq(0).attr("value");

			if(oSlideScrollContent_length == (sMinCount + 1))
			{				
				jQuery(oSlideScrollContents.obj[0]).append("<li class='slideScrollContent' value='" + targetLiValue + "'>" + html + "</li>");
				jQuery(oSlideScrollContents.obj[0]).css("width", ( sContentWidth * (oSlideScrollContent_length + 1)));
			}

            prevClicked = 1;

            jQuery(oSlideScrollContents.obj[0]).animate({
                left: '-=' + sContentWidth
            }, sDuration, function () {
				if(oSlideScrollContent_length != (sMinCount + 1))
				{
					jQuery(oSlideScrollContents.obj[0]).append("<li class='slideScrollContent' value='" + targetLiValue + "'>" + html + "</li>");
				}

				jQuery(oSlideScrollContents.obj[0]).css("left", (sContentWidth * -1));
				jQuery(oSlideScrollContents.obj[0]).children("li").eq(0).remove();
				
				if(oSlideScrollContent_length != (sMinCount + 1))
				{					
					jQuery(oSlideScrollContents.obj[0]).css("width", ( sContentWidth * oSlideScrollContent_length));
				}

                prevClicked = 0;                    
            });
        });

        jQuery(oNext.obj[0]).click(function () {
            if (nextClicked == 1 || oSlideScrollContent_length <= sMinCount) {
                return;
            }
			else if(!sIsRotate) {
				if(jQuery(oSlideScrollContents.obj[0]).children("li").eq(0).attr("value") == oSlideScrollContent_length){
					return;
				}
			}	
			

            nextClicked = 1;

            jQuery(oSlideScrollContents.obj[0]).animate({
                left: '+=' + sContentWidth
            }, sDuration, function () {
                var flag = jQuery(oSlideScrollContents.obj[0]).children("li").length - 1;

                var html = jQuery(oSlideScrollContents.obj[0]).children("li").eq(flag).html();
				var targetLiValue = jQuery(oSlideScrollContents.obj[0]).children("li").eq(flag).attr("value");
                jQuery(oSlideScrollContents.obj[0]).prepend("<li class='slideScrollContent' value='" + targetLiValue + "'>" + html + "</li>");
                jQuery(oSlideScrollContents.obj[0]).css("left", (sContentWidth * -1));
                jQuery(oSlideScrollContents.obj[0]).children("li").eq(jQuery(oSlideScrollContents.obj[0]).children("li").length - 1).remove();
                nextClicked = 0;
            });
        });

		if(sAutoRolling == true)
		{
			jQuery(oWrapper).hover(function(){ clearInterval(sTimer);}, function(){sTimer = setTimeout(function() { timer(); }, sTimerInterval);});
		}		
    }

	function timer(){
		clearTimeout(sTimer);
		
		if(sReverse == false){ jQuery(oNext.obj[0]).trigger("click");}
		else { jQuery(oPrev.obj[0]).trigger("click"); }

		sTimer = setTimeout(function() { timer(); }, sTimerInterval);
	}

    return init();
}