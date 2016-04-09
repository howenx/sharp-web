
$(function(){
        var count=0;
    /*改变产品数量*/
    $(".quantity-increase").on("click",function(){
        var t=$(this).parent().find('input[class*=quantity]');
        var aa = parseInt(t.val()) + 1;

        var li=$(this).parents("li");
        var restrictAmount=li.find(".restrictAmountCss").val();
        if(aa>restrictAmount){
            tip("本商品限制购买"+restrictAmount+"件");
            return;
        }


        var cartId=li.find(".cartId").val() ;
        var skuId=li.find(".skuId").val() ;
        var skuType=li.find(".skuType").val() ;
        var skuTypeId=li.find(".skuTypeId").val() ;
        var state=li.find(".state").val() ;

        var obj=new Object();
        obj.cartId=cartId;
        obj.skuId=skuId;
        obj.skuType=skuType;
        obj.skuTypeId=skuTypeId;
        obj.state=state;
        obj.amount=aa;
        $.ajax({
            type: 'POST',
            url: "/cart/add",
            contentType: "application/json; charset=utf-8",
            data : JSON.stringify(obj),
            dataType: 'json',
            error : function(request) {
                tip("修改购物车失败");
             },
            success: function(data) {
                console.log("data="+data);
                if (data!=""&&data!=null){
                    if(data.code==200) { //成功
                         //修改数量
                         t.val(aa);
                         li.find(".amount").val(aa);

                         var s = li.find(".price").html();
                         var d = aa * s;

                         var ss = li.find(".subtotal").html(d);
                         Total();
                         //检测商品总额限制
                         checkPostalLimit();

                    }else{
                         tip(data.message);
                    }
                }else{
                 tip("修改购物车失败");
                }
            }
        });


    });

    $(".quantity-decrease").on("click",function(){
        var t=$(this).parent().find('input[class*=quantity]');
        var aa = parseInt(t.val()) - 1;
        if(aa<=0){
            return;
        }

        var li=$(this).parents("li");
        var cartId=li.find(".cartId").val() ;
        var skuId=li.find(".skuId").val() ;
        var skuType=li.find(".skuType").val() ;
        var skuTypeId=li.find(".skuTypeId").val() ;
        var state=li.find(".state").val() ;

        var obj=new Object();
        obj.cartId=cartId;
        obj.skuId=skuId;
        obj.skuType=skuType;
        obj.skuTypeId=skuTypeId;
        obj.state=state;
        obj.amount=aa;
        $.ajax({
            type: 'POST',
            url: "/cart/add",
            contentType: "application/json; charset=utf-8",
            data : JSON.stringify(obj),
            dataType: 'json',
            error : function(request) {
                tip("修改购物车失败");
             },
            success: function(data) {
                console.log("data="+data);
                if (data!=""&&data!=null){
                    if(data.code==200) { //成功
                         //修改数量
                         t.val(aa);
                         li.find(".amount").val(aa);

                         var s = li.find(".price").html();
                         var d = aa * s;

                         var ss = li.find(".subtotal").html(d);
                         Total();
                         //检测商品总额限制
                          checkPostalLimit();

                    }else{
                         tip(data.message);
                    }
                }else{
                 tip("修改购物车失败");
                }
            }
        });
    })


    /*选择某一个*/
    var selectInputs = document.getElementsByClassName('check'); //所有勾选
    var checkAllInputs = document.getElementsByClassName('check-all'); //全勾选
    $(checkAllInputs).click(function(){
        var hiddenAreaDivs=document.getElementsByClassName('hiddenArea'); //所有保税区
        var hiddenSkuDivs=document.getElementsByClassName('hiddenSku'); //所有商品
        if($(this).prop("checked")==true){
            $(selectInputs).prop("checked",true);
            $(hiddenAreaDivs).find("input").attr("disabled",false);
            $(hiddenSkuDivs).find("input").attr("disabled",false);
        }else{
            $(selectInputs).prop("checked",false);
            $(hiddenAreaDivs).find("input").attr("disabled",true);
            $(hiddenSkuDivs).find("input").attr("disabled",true);
        }
        Total();
        //检测商品总额限制
        checkPostalLimit();

    })

    /*checkBox 点击*/
    $(selectInputs).change(function(){
        if($(this).hasClass("areac")){
            if($(this).prop("checked")==true){ //保税区
                $(this).parents(".cart-goods-area").next().find("input[type=checkbox]").prop("checked",true);
                if($(".cart-goods .check:checked").length==selectInputs.length){
                    $(checkAllInputs).prop("checked",true);
                }

                //隐藏域
                $(this).parents(".cart-goods-area").find(".hiddenArea").find("input").attr("disabled",false); //
                $(this).parents(".cart-goods-area").next().find(".hiddenSku").find("input").attr("disabled",false);

            }else{
                $(checkAllInputs).prop("checked",false);
                $(this).parents(".cart-goods-area").next().find("input[type=checkbox]").prop("checked",false);
                //隐藏域
                $(this).parents(".cart-goods-area").find(".hiddenArea").find("input").attr("disabled",true); //
                $(this).parents(".cart-goods-area").next().find(".hiddenSku").find("input").attr("disabled",true);

            }
        }else if($(this).hasClass("check-one")){
            var input1 = $(this).parents("ul").find("input[type=checkbox]:checked").length;
            if($(this).prop("checked")==true) {
                var input2 = $(this).parents("ul").find("input[type=checkbox]").length;
                if (input1 == input2) {
                    $(this).parents("ul").prev().find(".check").prop("checked", true);
                    if ($(".cart-goods .check:checked").length == selectInputs.length) {
                        $(checkAllInputs).prop("checked", true);
                    } else {
                        $(checkAllInputs).prop("checked", false);
                    }
                }
                //隐藏域
                 $(this).parents("ul").prev().find(".hiddenArea").find("input").attr("disabled",false);
                 $(this).parents("li").find(".hiddenSku").find("input").attr("disabled",false);

            }else {
                $(checkAllInputs).prop("checked", false);
                $(this).parents(".cart-goods-list").prev().find(".check").prop("checked",false);

                //隐藏域
                if(input1<=0){ //保税区内没有选中的
                    $(this).parents("ul").prev().find(".hiddenArea").find("input").attr("disabled",true);
                }
                $(this).parents("li").find(".hiddenSku").find("input").attr("disabled",true);
            }
        }
        Total();
        //检测商品总额限制
        checkPostalLimit();
    })

    /*金额小计*/
    function setTotal(){
        /*var s=0;*/
        var v=0;
        $(".cart-product-number").each(function(){
            s+=parseInt($(this).find('input[class*=quantity]').val())*parseFloat($(this).siblings().find('span[class*=price]').text());
        });
        <!--计算分数-->
        $("input[type='text']").each(function(){
            v += parseInt($(this).val());
        });
        $(".cart-product-price").each(function(){
            $(this).find(".subtotal").html(s.toFixed(2));
        });
    }
    function funss(){
        $(".check-one").each(function(){
            count++
        });
        $("#cartCount").html(count);
    }
     funss();

   /*计算总额*/
    function Total(){
        var total =0.00;
        var v =0;
        var n =0;
        /*计算总钱数*/
        $(".check-one:checked").each(function(){
            var ts = $(this).parents("li").find(".subtotal").html();
            total+=parseFloat(ts);
        });
        $(".total").html(total.toFixed(2));
        /*计算总份数*/
        $(".check-one:checked").each(function(){
            v+= parseInt($(this).parents("li").find(".quantity").val());
        });
        $("#selectedTotal").html(v);
        /*计算购物车的数量*/

        //计算行邮税
         $(".areaAndSku").each(function(){
            var fee =0.00;
            var ckeckedSku=$(this).find("ul").find("input[type=checkbox]:checked");
             ckeckedSku.each(function(){
                var ts = $(this).parents("li").find(".subtotal").html();//小计
                var ptr = $(this).parents("li").find(".postalTaxRateCss").val();
                fee+=parseFloat(ts)*parseFloat(ptr)/100;
             });
             if(fee>0){
                $(this).find(".postalCss span").html(fee.toFixed(2));
                $(this).find(".postalCss").show();
             }else{
                $(this).find(".postalCss").hide();
             }

         });

    }

    //检测商品总额限制
    function checkPostalLimit(){
       var limitFlag=false;
       var selectAreaNum=0;
       $(".areaAndSku").each(function(){
         var invArea=$(this).find(".invAreaCss").val();

         var total =0.00;
          var postalLimit=$(this).find(".postalLimit").val();
          var ckeckedSku=$(this).find("ul").find("input[type=checkbox]:checked");
          ckeckedSku.each(function(){
             var ts = $(this).parents("li").find(".subtotal").html();
              if('K'!=invArea){ //海外直邮不限制
                 total+=parseFloat(ts);
             }
          });
          if(!limitFlag&&total>postalLimit){
             $("#hint-hd").html("友情提示 : "+$(this).find(".area").html()+"直邮商品总额超过¥"+postalLimit);
             limitFlag=true;
          }
          if(ckeckedSku.length>0){
              selectAreaNum++;
              if(selectAreaNum>=2){
                 $("#hint-hd").html("友情提示 : 单次购买,只能购买同一保税区的商品");
                 return false;
              }
          }



       });

       if(limitFlag==false&&selectAreaNum<2){
            $("#hint-hd").html("友情提示 : 同一保税区商品总额有限制");
            if($("#selected").hasClass("settleBtn")==false){
                $("#selected").addClass("settleBtn").removeClass("discolour");
            }
       } else{
            //超出限制结算按钮不可用
            $("#selected").removeClass("settleBtn").addClass("discolour");

       }

    }



});

//删除购物车
function delCart(cartId){
    if (window.confirm("确定删除吗?")) {
        console.log("cartId="+cartId);
        $.ajax({
              type :"GET",
              url : "/cart/del/"+cartId,
              contentType: "application/json; charset=utf-8",
              error : function(request) {
                  tip("删除失败!");
              },
              success: function(data) {
                 console.log("data="+data);
                  if (data!=""&&data!=null&&data.code==200){ //删除成功
                      var li=$("#li"+cartId);
                      var ul=li.parents("ul");
                      li.remove();
                      if(ul.has("li").length <= 0){
                            ul.prev().parents(".areaAndSku").remove();

                      }
                  } else tip("删除失败!");

              }
         });
    }
};

//去结算
$(document).on("click",".settleBtn",function(){

     if($("input:checkbox[name=check_item]:checked").length<=0){
        tip("请选择商品");
        return;
     }

    //提交表单
    $("#settleForm").submit();

});

//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}




