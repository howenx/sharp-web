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
})