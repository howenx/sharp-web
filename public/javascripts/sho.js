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
        var s = $(".cart-goods-list li").eq($(this).parents("li").index()).find(".price").html();
        var d = aa * s;

        var ss = $(".cart-goods-list li").eq($(this).parents("li").index()).find(".subtotal").html(d);

        Total();
    })

    $(".quantity-decrease").on("click",function(){
        var t=$(this).parent().find('input[class*=quantity]');
        t.val(parseInt(t.val())-1);
        if(parseInt(t.val())<0){
            t.val(0);
        }
        var aa = parseInt(t.val());
        var s = $(".cart-goods-list li").eq($(this).parents("li").index()).find(".price").html();
        var d = aa * s;

        var ss = $(".cart-goods-list li").eq($(this).parents("li").index()).find(".subtotal").html(d);

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



   /*ѡ��ĳһ��*/
    var selectInputs = document.getElementsByClassName('check'); // ���й�ѡ��
    var checkAllInputs = document.getElementsByClassName('check-all') // ȫѡ��
    for(var i = 0; i < selectInputs.length; i++ ){
        selectInputs[i].onclick = function () {
            if (this.className.indexOf('check-all') >= 0) { //�����ȫѡ��������е�ѡ���ѡ��
                for (var j = 0; j < selectInputs.length; j++) {
                    selectInputs[j].checked = this.checked;
                }
            }
            if (!this.checked) { //ֻҪ��һ��δ��ѡ����ȡ��ȫѡ���ѡ��״̬
                for (var i = 0; i < checkAllInputs.length; i++) {
                    checkAllInputs[i].checked = false;
                }
            }
            Total();//ѡ������ܼ�
        }
    }



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



