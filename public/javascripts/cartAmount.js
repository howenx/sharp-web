
//获取购物车数量
$(document).ready(function() {
    var cartAmountSpan=$("#cartAmountSpan");
    if(null!=cartAmountSpan&&"undefined"!=typeof(cartAmountSpan)){
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
            html+='<img src="/assets/images/z-l.png" data-echo="'+item.itemImg+'">'+
            '</p>'+
            '<p class="title-hd">'+item.itemTitle+'</p>'+
                    '<span class="title-fd">'+
                    '<i class="naw">￥'+item.itemPrice+'</i>';
            if(null!=item.itemDiscount&&false==item.itemDiscount.toString().startsWith("10")){
                html+=' <i class="agio">'+item.itemSrcPrice+'</i>'+
                '<b>'+item.itemDiscount+'折</b>';
            }
            html+='</span>';

            if(item.state== "P"){
                html+='<p class="obstruct"><span>即将上线</span></p>';
            }
            html+=' </a>';
        }

        if(item.itemType == "pin"){
            html+='<a class="redirect-app"  href="/detail/'+item.itemUrl+'">'+
                   '<p class="product-box">'+
                   '<span class="product-top">'+item.endAt+'</span>'+
                   '</p>'+

                    '<p class="figure">'+
                        '<img src="/assets/images/l-120.png" data-echo="'+item.itemImg+'">'+
                    '</p>'+

                    '<p class="title-hd">'+item.itemTitle+'</p>'+
                         '<span class="title-fd">'+
                         '<i class="naw">￥'+item.itemPrice+'</i><i class="minimum">最低</i>';
                      if(null!=item.itemDiscount&&false==item.itemDiscount.toString().startsWith("10")){
                         html+='<i class="rate">低至'+item.itemDiscount+'折</i>';
                      }
            html+='</span>'+
             '</a>';
        }


    }
    html+='</li>';

    return html;
};




