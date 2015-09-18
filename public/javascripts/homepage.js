$(function() {
	$(window).resize(function() {
		// resize_margin();
	});
	function resize_margin(){
		var margin;
		if(document.body.clientWidth>620){
			margin= (document.body.clientWidth-600)/2;
			
		}else if(document.body.clientWidth<620 && document.body.clientWidth>300){
			margin= (document.body.clientWidth-300)/2;
		}
		$('.brand_list_box').css({'margin-left':margin+'px','margin-right':margin+'px'});
	}
	// resize_margin();
	
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
	
	/**down mask**/
	var url1 = '/images/personal.png';
	var url2 = '/images/message.png';
	var url3 = '/images/service.png';
	var url4 = '/images/gray-dot-line.png';
	var str = '<style>'+
	'span.span-personal {'+
	'	float: left;'+
	'	position: relative;'+
	'	left: 10%;'+
	'	width: 60px;'+
	'	height:60px;'+
	'	background: url('+url1+') no-repeat top center;'+
	'	background-size: contain;'+
	'	background-color: #fff;'+
	'}'+
	'.span-personal::after {'+
	'	content: "我";'+
	'   display: block;'+
	'   height: 20px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 20px;'+
	'   top: 65px;'+
	'   left: 20px;'+
	'   font-size: medium;'+
	'}'+
	'.div-personal {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:33%;'+
	'	height:80px;'+
	'	background-color: #fff;'+
	'}'+
	'.div-message {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:33%;'+
	'	height:80px;'+
	'	background-color: #fff;'+
	'}'+
	'span.span-message {'+
	'	float: left;'+
	'	position: relative;'+
	'	left: 45%;'+
	'	width: 60px;'+
	'	height:60px;'+
	'	background: url('+url2+') no-repeat top center;'+
	'	background-size: contain;'+
	'	background-color: #fff;'+
	'}'+
	'.span-message::after {'+
	'	content: "消息";'+
	'   display: block;'+
	'   height: 20px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 20px;'+
	'   top: 65px;'+
	'   left: 15px;'+
	'   font-size: medium;'+
	'}'+
	'.div-service {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:34%;'+
	'	height:80px;'+
	'	background-color: #fff;'+
	'}'+
	'span.span-service {'+
	'	float: right;'+
	'	position: relative;'+
	'	right: 10%;'+
	'	width: 60px;'+
	'	height:60px;'+
	'	background: url('+url3+') no-repeat top center;'+
	'	background-size: contain;'+
	'	background-color: #fff;'+
	'}'+
	'.span-service::after {'+
	'	content: "客服";'+
	'   display: block;'+
	'   height: 20px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 20px;'+
	'   top: 65px;'+
	'   left: 15px;'+
	'   font-size: medium;'+
	'}'+
	'.gray-dot-line {'+
	'	margin-left:5%;'+
	'   margin-right:5%;'+
	'   margin-top: 5px;'+
	'   display:block;'+
	'   width:90%;'+
	'   height:5px;'+
	'   background-color: #fff;'+
	'   background-size: contain;background: url('+url4+') repeat center center;'+
	'}'+
	'.down-title {'+
	'	background: url("/images/logo_hmm.png") no-repeat center center;'+
	'	position: relative;'+
	'	height: 40px;'+
	'	margin-bottom: 10px;'+
	'	margin-top: 10px;'+
	'	display: block;'+
	'	background-size: contain;'+
	'}'+
	'.down_title_up {'+
	'	background: url("/images/up.png") no-repeat left center;'+
	'	width: 40px;'+
	'	height: 100%;'+
	'	display: inline-block;'+
	'	background-size: contain;'+
	'	position: relative;'+
	'	float:left;'+
	'	left:15px;'+
	'}'+
	'.down_title_basket {'+
	'	background: url("/images/basket_top.png") no-repeat right center;'+
	'	width: 40px;'+
	'	height: 100%;'+
	'	background-size: contain;'+
	'	display: inline-block;'+
	'	position: relative;'+
	'	float:right;'+
	'	right:15px;'+
	'}'+
	'</style>'+
	'<div id="down-mask">'+
		'<div style="width:100%;height:100%;background-color: black;z-index: 300;opacity: 0.6;display: block;position: absolute;top: 0px;">'+
		'</div>'+
		'<div style="width:100%;height:165px;background-color: #fff;z-index: 301;display: block;position: absolute;top: 0px;">'+
			'<div class ="div-personal">'+
				'<span class="span-personal"></span>'+
			'</div>'+
			'<div class ="div-message">'+
				'<span class= "span-message" style="width: 60px;height: 60px;"></span>'+
			'</div>'+
			'<div class ="div-service">'+
				'<span class= "span-service" style="width: 60px;height: 60px;"></span>'+
			'</div>'+
			'<div class ="gray-dot-line"></div>'+
			'<div class="down-title">'+
				'<div class="down_title_up"></div>'+
				'<div class="down_title_basket"></div>'+
			'</div>'+
		'</div>'+
	'</div>';
	var str600 = '<style>'+
	'span.span-personal {'+
	'	height: 30px;'+
	'	float: left;'+
	'	position: relative;'+
	'	left: 10%;'+
	'	width: 30px;'+
	'	background: url('+url1+') no-repeat center center;'+
	'	background-size: contain;'+
	'}'+
	'.span-personal::after {'+
	'	content: "我";'+
	'   display: block;'+
	'   height: 10px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 10px;'+
	'   top: 35px;'+
	'   left: 22%;'+
	'   font-size: 5px;'+
	'}'+
	'.div-personal {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:33%;'+
	'	height:40px;'+
	'	background-color: #fff;'+
	'}'+
	'.div-message {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:33%;'+
	'	height:40px;'+
	'	background-color: #fff;'+
	'}'+
	'span.span-message {'+
	'	height: 30px;'+
	'	float: left;'+
	'	position: relative;'+
	'	left: 45%;'+
	'	width: 30px;'+
	'	background: url('+url2+') no-repeat top center;'+
	'	background-size: contain;'+
	'	background-color: #fff;'+
	'}'+
	'.span-message::after {'+
	'	content: "消息";'+
	'   display: block;'+
	'   height: 10px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 10px;'+
	'   top: 35px;'+
	'   left: 1px;'+
	'   font-size: 5px;'+
	'}'+
	'.div-service {'+
	'	margin-top: 10px;'+
	'	display: inline-block;'+
	'	width:34%;'+
	'	height:40px;'+
	'	background-color: #fff;'+
	'}'+
	'span.span-service {'+
	'	height: 30px;'+
	'	float: right;'+
	'	position: relative;'+
	'	right: 10%;'+
	'	width: 30px;'+
	'	background: url('+url2+') no-repeat top center;'+
	'	background-size: contain;'+
	'	background-color: #fff;'+
	'}'+
	'.span-service::after {'+
	'	content: "客服";'+
	'   display: block;'+
	'   height: 10px;'+
	'   float: left;'+
	'   position: relative;'+
	'   color: #678826;'+
	'   line-height: 10px;'+
	'   top: 35px;'+
	'   left: 1px;'+
	'   font-size: 5px;'+
	'}'+
	'.gray-dot-line {'+
	'	margin-left:5%;'+
	'   margin-right:5%;'+
	'   margin-top: 5px;'+
	'   display:block;'+
	'   width:90%;'+
	'   height:5px;'+
	'   background-color: #fff;'+
	'   background-size: contain;background: url('+url4+') repeat center center;'+
	'}'+
	'.down-title {'+
	'	background: url("/images/logo_hmm.png") no-repeat center center;'+
	'	position: relative;'+
	'	height: 25px;'+
	'	margin-bottom: 10px;'+
	'	margin-top: 10px;'+
	'	display: block;'+
	'	background-size: contain;'+
	'}'+
	'.down_title_up {'+
	'	background: url("/images/up.png") no-repeat left center;'+
	'	width: 40px;'+
	'	height: 100%;'+
	'	display: inline-block;'+
	'	background-size: contain;'+
	'	position: relative;'+
	'	float:left;'+
	'	left:15px;'+
	'}'+
	'.down_title_basket {'+
	'	background: url("/images/basket_top.png") no-repeat right center;'+
	'	width: 40px;'+
	'	height: 100%;'+
	'	background-size: contain;'+
	'	display: inline-block;'+
	'	position: relative;'+
	'	float:right;'+
	'	right:15px;'+
	'}'+
	'</style>'+
	'<div id="down-mask">'+
		'<div style="width:100%;height:100%;background-color: black;z-index: 300;opacity: 0.6;display: block;position: absolute;top: 0px;">'+
		'</div>'+
		'<div style="width:100%;height:105px;background-color: #fff;z-index: 301;display: block;position: absolute;top: 0px;">'+
			'<div class ="div-personal">'+
				'<span class="span-personal"></span>'+
			'</div>'+
			'<div class ="div-message">'+
				'<span class= "span-message" style="width: 30px;height: 30px;"></span>'+
			'</div>'+
			'<div class ="div-service">'+
				'<span class= "span-service" style="width: 30px;height: 30px;"></span>'+
			'</div>'+
			'<div class ="gray-dot-line"></div>'+
			'<div class="down-title">'+
				'<div class="down_title_up"></div>'+
				'<div class="down_title_basket"></div>'+
			'</div>'+
		'</div>'+
	'</div>';
	$('.top_title_down').click(function(){
		$("body").css({overflow:"hidden"}); 
		if(document.body.clientWidth>620){
			$('body').append(str);
			
		}else if(document.body.clientWidth<620 && document.body.clientWidth>300){
			$('body').append(str600);
		}
		
	});
	$(document).on("click", "div.down_title_up", function() {
		$('#down-mask').remove();
		$("body").css({overflow:"auto"}); 
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

// var iframe =document.getElementById('win');
// if (iframe.attachEvent) {
// 	iframe.attachEvent("onload", function() {
// 		$('#win').height($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 		$('#slider').height($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 		console.log($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 	});
// } else {
// 	iframe.onload = function() {
// 		$('#win').height($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 		$('#slider').height($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 		console.log($('#win')[0].contentDocument.body.parentElement.offsetHeight);
// 	};
// }
