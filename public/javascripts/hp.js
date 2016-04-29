$(function() {



	//获取window的高度
	//滚动条滚动的垂直距离大于窗口高度 才是小火箭现身的最佳时机
	$(window).scroll(function(e) {
		if($(window).scrollTop() > $(window).height()){
			$('.top').show();
		}else{
			$('.top').hide();
		}
	});

	//点击火箭返回顶部
	$('.top').click(function(e) {
		$('html,body').stop().animate({'scrollTop':'0'},500);
	});

	$('.more').click(function(){
		$(this).hide();
		$('.later').show();

	});

	$('.download b').click(function(){
		$(this).parents('.download-box').hide();
	});




	/*('.foot .buy').click(function(){
		$(this).children('.icon02').css({'backgroundImage':'url(images/02.png)'});
	})*/


	$(window).load(function(){
		$("#loading").hide();
		$(".fixed-box").css({"position":"relative"});
	});

    if(typeof(echo)!="undefined"&&null!=echo){
	    echo.init();
	}
});



