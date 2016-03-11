/**
 * Created by bentudou on 2015/11/9
 */
$(function(){
        var count=0;
    /*�ı��Ʒ����*/
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



    /* ���ɾ����ťɾ����Ʒ*/
     $(".cart-del-btn").on("click",function(){
         var r=confirm(" Do you want to remove the goods")
         if (r==true)
         {
             $(this).parents("li").hide();
         }
         else {  }
     });
    /***** checkBox 点击******/
    var selectInputs = document.getElementsByClassName('check');
    var checkAllInputs = document.getElementsByClassName('check-all');
    $(checkAllInputs).click(function(){
        if($(this).prop("checked")==true){
            $(selectInputs).prop("checked",true);
        }else{
            $(selectInputs).prop("checked",false);
        }
        Total();
    })

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

    /*���С��*/
    function setTotal(){
        /*var s=0;*/
        var v=0;
        $(".cart-product-number").each(function(){
            s+=parseInt($(this).find('input[class*=quantity]').val())*parseFloat($(this).siblings().find('span[class*=price]').text());
        });
        <!--�������-->
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

    <!--�����ܶ�-->
    function Total(){
        var total =0.00;
        var v =0;
        var n =0;
        /*������Ǯ��*/
        $(".check-one:checked").each(function(){
            var ts = $(this).parents("li").find(".subtotal").html();
            total+=parseFloat(ts);
        });
        $(".total").html(total.toFixed(2));
        /*�����ܷ���*/
        $(".check-one:checked").each(function(){
            v+= parseInt($(this).parents("li").find(".quantity").val());
        });
        $("#selectedTotal").html(v);
        /*���㹺�ﳵ������*/

    }


})



