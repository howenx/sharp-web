
//获取购物车数量
$(document).ready(function() {
    var cartAmountSpan=$("#cartAmountSpan");
    if(null!=cartAmountSpan&&"undefined"!=typeof(cartAmountSpan)&&cartAmountSpan.length>0){
        $.ajax({
              type :"GET",
              url : "/cart/amount",
              contentType: "application/json; charset=utf-8",
              error : function(request) {
              },
              success: function(data) {
                  if (data!=""&&data!=null&&null!=data.message){ //成功
                        if(data.message.code==200){
                             $("#cartAmountSpan i").html(data.cartNum);
                        }
                   }
              }
         });
     }
});

//公用部分展示themeItem商品li
function themeItemListHtml(rowList){
    var html='<li class="clearfix">';
    for(var m in rowList){
        var item=rowList[m];
        if(item.itemType == "item" || item.itemType == "vary" || item.itemType == "customize"){
            html+='<a class="redirect-app" href="/detail/'+item.itemUrl+'">'+
            '<p class="figure figure-f">';
            if(item.state != "Y"&&item.state != "P"){
                html+='<span>已售罄</span>';
            }

//            html+='<img src="/assets/images/z-l.png" data-echo="'+item.itemImg+'">'+
            html+='<img src="'+item.itemImg+'">'+
            '</p>'+
            '<p class="title-hd">'+item.itemTitle+'</p>'+
                    '<span class="title-fd">'+
                    '<i class="naw">￥'+item.itemPrice+'</i>';

            if(null!=item.itemDiscount&&item.itemDiscount.indexOf("10")!=0){
                html+=' <i class="agio">'+item.itemSrcPrice+'</i>'+
                '<b>'+item.itemDiscount+'折</b>';
            }
            html+='</span>';
            if(item.state== "P"){
                html+=' <p class="obstruct"><span><img src="/assets/images/hmm_goods_yushou.png"/></span></p>';
            }
            html+=' </a>';
        }
        if(item.itemType == "pin"){
            html+='<a class="redirect-app"  href="/detail/'+item.itemUrl+'">'+
                   '<p class="product-box">'+
                   '<span class="product-top"><i>'+item.endAt+'</i></span>'+
                   '</p>'+

                    '<p class="figure">'+
//                        '<img src="/assets/images/l-120.png" data-echo="'+item.itemImg+'">'+
                          '<img src="'+item.itemImg+'">'+
                    '</p>'+

                    '<p class="title-hd">'+item.itemTitle+'</p>'+
                         '<span class="title-fd">'+
                         '<i class="naw">￥'+item.itemPrice+'</i><i class="minimum">最低</i>';
                    if(null!=item.itemDiscount&&item.itemDiscount.indexOf("10")!=0){
                         html+='<i class="rate">低至'+item.itemDiscount+'折</i>';
                    }
            html+='</span>'+
             '</a>';
        }

    }
    html+='</li>';

    return html;
};

function getRecommendSku(position){
        $.ajax({
              type :"GET",
              url : "/recommend/"+position,
              contentType: "application/json; charset=utf-8",
              error : function(request) {
              },
              success: function(data) {
                  if(null!=data&&data.length != 0){
                      for(var i in data){
                          var rowList=data[i];
                          var html=themeItemListHtml(rowList);
                          $("#recommendUl").append(html);

                    }
                }
              }
         });
}




