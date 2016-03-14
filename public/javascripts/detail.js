$(function() {
	$('.classify ul li').click(function(){
		$(this).addClass('current').sibling().removeClass('current');
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



	//切换

	$(window).scroll(function(e) {
		if($(window).scrollTop() >880){
			$('.detail-tabpanel').addClass('detail-tabpanel-fixed');
			$('.nav_ban_detail').hide()
		}else{
			$('.detail-tabpanel').removeClass('detail-tabpanel-fixed');
			$('.nav_ban_detail').show()
		}
	});

	$('.tabpanel-tabs ul li').click(function(){
		$(this).addClass('current').siblings().removeClass('current');

		if ($(this).hasClass('goods-para')) {
			$('body').scrollTop($('.sku-measure').offset().top);
		}
		if ($(this).hasClass('pic-tex')) {
			$('body').scrollTop($('.conner').offset().top);
		}
		if ($(this).hasClass('hot-sale')) {
			$('body').scrollTop($('.hot').offset().top);
		}

	})

	$('.top').click(function(e) {
		$('html,body').stop().animate({'scrollTop':'0'},500);
	});

	/*收藏*/
	var off = true;
    var tim = null;
	$('.soul').click(function(){

		clearInterval( tim );
		if(off){
			$(this).addClass('min');
			$('.like-s').show();

			tim = setInterval(function () {
				$('.like-s').hide();
			},1000);

			$('.like-x').hide();
			off =false;

		}else {
			$(this).removeClass('min');
			$('.like-s').hide();
			$('.like-x').show();
			tim = setInterval(function () {
				$('.like-x').hide();
			},1000);
			off =true;
		}



	})

//    弹出
	$(".toup .con").animate({
		bottom:0,
	},1000);
	$(".bg").click(function(){
		$(".toup .con").animate({
			bottom:-295,
		},1000,function(){
			$(".toup").css("display","none");
		});
	})


})
