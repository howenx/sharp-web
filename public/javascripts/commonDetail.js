$(function(){
	//主Sku商品下架
	if($("#master-sku").length == 0){
		$(".finish-box").css("display","block");
		$(".add_cart .cartAdd").removeClass('cartAdd');
		$(".buy_btn .buyBtnCss").removeClass('buyBtnCss');
	}
})
