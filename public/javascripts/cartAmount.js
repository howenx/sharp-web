
//获取购物车数量
$(document).ready(function() {
    $.ajax({
          type :"GET",
          url : "/cart/amount",
          contentType: "application/json; charset=utf-8",
          error : function(request) {
          },
          success: function(data) {
             console.log("data="+data);
              if (data!=""&&data!=null){ //成功
                    if(data.message.code=200){
                         $("#cartAmountSpan").html(data.cartNum);
                    }
               }
          }
     });
});


