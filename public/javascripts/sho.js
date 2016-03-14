
$(function(){
        var count=0;
    /*改变产品数量*/
    $(".quantity-increase").on("click",function(){
        var t=$(this).parent().find('input[class*=quantity]');
        var aa = parseInt(t.val()) + 1;
        t.val(aa);
        var s = $(this).parents("li").find(".price").html();
        var d = aa * s;

        var ss = $(this).parents("li").find(".subtotal").html(d);

        Total();
    })

    $(".quantity-decrease").on("click",function(){
        var t=$(this).parent().find('input[class*=quantity]');
        t.val(parseInt(t.val())-1);
        if(parseInt(t.val())<0){
            t.val(0);
        }
        var aa = parseInt(t.val());
        var s = $(this).parents("li").find(".price").html();
        var d = aa * s;

        var ss = $(this).parents("li").find(".subtotal").html(d);

        Total();
    })



    /*点击删除按钮删除商品*/
     $(".cart-del-btn").on("click",function(){
         var r=confirm(" Do you want to remove the goods")
         if (r==true)
         {
             $(this).parents("li").hide();
         }
         else {  }
     });
    /*选择某一个*/
    var selectInputs = document.getElementsByClassName('check'); //所有勾选
    var checkAllInputs = document.getElementsByClassName('check-all'); //全勾选
    $(checkAllInputs).click(function(){
        if($(this).prop("checked")==true){
            $(selectInputs).prop("checked",true);
        }else{
            $(selectInputs).prop("checked",false);
        }
        Total();
    })

    /*checkBox 点击*/
    $(selectInputs).change(function(){
        if($(this).hasClass("areac")){
            if($(this).prop("checked")==true){
                $(this).parents(".cart-goods-area").next().find("input[type=checkbox]").prop("checked",true);
                if($(".cart-goods .check:checked").length==selectInputs.length){
                    $(checkAllInputs).prop("checked",true);
                }
            }else{
                $(checkAllInputs).prop("checked",false);
                $(this).parents(".cart-goods-area").next().find("input[type=checkbox]").prop("checked",false);
            }
        }else if($(this).hasClass("check-one")){
            if($(this).prop("checked")==true) {
                var input1 = $(this).parents("ul").find("input[type=checkbox]:checked").length;
                var input2 = $(this).parents("ul").find("input[type=checkbox]").length;
                if (input1 == input2) {
                    $(this).parents("ul").prev().find(".check").prop("checked", true);
                    if ($(".cart-goods .check:checked").length == selectInputs.length) {
                        $(checkAllInputs).prop("checked", true);
                    } else {
                        $(checkAllInputs).prop("checked", false);
                    }
                }
            }else {
                $(checkAllInputs).prop("checked", false);
                $(this).parents(".cart-goods-list").prev().find(".check").prop("checked",false);
            }
        }
        Total();
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

    }


})



