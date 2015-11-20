$(function() {


    $('.add_cart').click(function(){


	})















	$('#slide').on('cycle-next', function(event, opts) {
		if (opts.slides[opts.currSlide].firstElementChild.src === '') {
			opts.slides[opts.currSlide].firstElementChild.src = "/images/sku-slide/" + opts.currSlide + ".jpg"
		}
	});
	$('#slide').on('cycle-pager-activated', function(event, opts) {
		if (opts.slides[opts.currSlide].firstElementChild.src === '') {
			opts.slides[opts.currSlide].firstElementChild.src = "/images/sku-slide/" + opts.currSlide + ".jpg"
		}
	})
	$(".tab-item").click(function() {
		$avtive = $(".tab-item.active");
		$(this).addClass('active');
		$avtive.removeClass('active');
		if ($(this).hasClass('goods-para')) {
			$('body').scrollTop($('.sku-measure').offset().top);
		}
		if ($(this).hasClass('pic-tex')) {
			$('body').scrollTop($('.detail-tabpanel').offset().top);
		}
	})
	$('#rocket').click(function() {
		$('body,html').animate({
			scrollTop: 0
		}, 500);
		return false;
	})

	function countDown(endtime, now) {
		var end_time = new Date(endtime).getTime(), //月份是实际月份-1
			current_time = new Date(now).getTime(),
			sys_second = (end_time - current_time) / 1000;
		var timer = setInterval(function() {
			if (sys_second > 0) {
				sys_second -= 1;
				var day = Math.floor((sys_second / 3600) / 24);
				var hour = Math.floor((sys_second / 3600) % 24);
				var minute = Math.floor((sys_second / 60) % 60);
				var second = Math.floor(sys_second % 60);
				//timer
				$('#timer').text(
					(day == 0 ? "" : day + "天") +
					(hour < 10 ? "0" + hour + "小时" : hour + "小时") +
					(minute < 10 ? "0" + minute + "分" : minute + "分") +
					(second < 10 ? "0" + second + "秒" : second + "秒")
				);
			} else {
				$('.timer_box').remove();
				clearInterval(timer);
			}
		}, 1000);
	}
	countDown($('#end_time').val(), $('#current_time').val());
})
window.onscroll = function(e) {
	var scrollTop = document.body.scrollTop;
	if (scrollTop >= $('.details').offset().top) {
		$('#rocket').css({
			'opacity': 1
		});
		$('.sku-detail-buy').css({
			'opacity': 1
		});
	} else {
		$('#rocket').css({
			'opacity': 0
		});
	}
	///sku
	if (scrollTop >= 1) {
		$('.sku-detail-buy').css({
			'opacity': 1
		});
	} else {
		$('.sku-detail-buy').css({
			'opacity': 0
		});
	}
	///detail tab
	if(scrollTop>=$('.detail-tabpanel').offset().top){
		$('#TabpanelOccupying').addClass('detail-tabpanel-fixed');
	}else{
		$('#TabpanelOccupying').removeClass('detail-tabpanel-fixed');
	}
	
}
