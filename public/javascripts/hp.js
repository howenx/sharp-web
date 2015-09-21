$(function() {
	$(document).on("click", "#topbanner-close", function() {
		$(this).parent().remove();
	})
	$(document).on("click", ".taba li", function() {
		$('.taba li').each(function(){
    		$(this).removeClass("active");
  	  	});
		$(this).addClass("active");
		console.log($(this).hasClass("account"));
		if($(this).children().first().hasClass("account")){
			$('#account_loginForm').css('display','block');
			$('#cell_loginForm').css('display','none');
		}else if($(this).children().first().hasClass("cellphone")){
			$('#account_loginForm').css('display','none');
			$('#cell_loginForm').css('display','block');
		}
	})
    
    /*************slide*******************/
	$('#slide').on('cycle-next', function(event, opts) {
		if(opts.slides[opts.currSlide].firstElementChild.src===''){
			opts.slides[opts.currSlide].firstElementChild.src="/images/slide_banners/"+opts.currSlide+".jpg"
		}
	});
    
	//nav click choose a href..
	$('.mainNav li').click(function(){
		$('.mainNav li').each(function(){
    		$(this).removeClass("cur");
  	  	});
		$(this).addClass("cur");
	});
	$('#rocket').click(function(){
		$('body,html').animate({scrollTop:0},500); 
		return false; 
	});
	///header search btn click
	$('#searchBtn').click(function(){
		if($('#M_Mask').css('display')==='none'){
			$('#M_headBar').css('z-index','1000');
			$('#M_Mask').css('display','');
			$('#M_search').css('display','');
			$('#Msearch_form').children().last().css('color','rgb(66, 66, 66)');
			$('body,html').css('overflow','hidden');
		}else{
			$('#M_headBar').css('z-index','998');
			$('#M_Mask').css('display','none');
			$('#M_search').css('display','none');
			$('#Msearch_form').children().last().css('color','rgb(153, 153, 153)');
			$('body,html').css('overflow','auto');
		}		
	});
});
window.onscroll = function(e) {
	var scrollTop = document.body.scrollTop;
	if (document.body.clientWidth > 620) {
		if (scrollTop >= 500) {
			$('#rocket').css({
				'opacity': 1
			});
		} else {
			$('#rocket').css({
				'opacity': 0
			});
		}
	} else {
		if (scrollTop >= 50) {
			$('#rocket').css({
				'opacity': 1
			});
		} else {
			$('#rocket').css({
				'opacity': 0
			});
		}
	}
}

