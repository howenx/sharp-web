$(function() {



	//获取window的高度
	$(window).scroll(function(e) {
		if($(window).scrollTop() > $(window).height()){
			$('.top').show();
		}else{
			$('.top').hide();
		}
	});

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






	$(window).load(function(){
		$("#loading").hide();
	});


    if(typeof(echo)!="undefined"&&null!=echo){
	    echo.init();
	}

});



