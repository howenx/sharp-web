
$(document).ready(function(){
    commentPage(1,2);
    commentPage(1,3);
    commentPage(1,4);
});

//点击评论页签
function commentTab(skuType,skuTypeId,commentType){
     var index=commentType-1;
     sessionindex = index;
     $('.scroll-wrap .scroll-content section').hide();
     $("#commentUl"+commentType).parents("section").show();

     $('.nav-tab-top li').find("a").removeClass('current');
     $("#tabLi"+commentType).find("a").addClass('current');

     $("#commentType").val(commentType);
    // $(this).addClass('current').parent().siblings().children('a').removeClass('current');
    var curPage=$("#curPage"+commentType).val();
    if(curPage>0){ //说明请求过了,只切换当前状态
      return false;
    }
    commentPage(1,commentType);
};

//绘制评论
function paintComment(comment,commentType){
    var html="";
    if(commentType==4){
        if(null!=comment.picture&&""!=comment.picture){
             html+='<img src="'+comment.picture+'" alt="'+comment.content+'">';
        }
    }else{
        html='<li><div class="hd clearfix">'+
                   '<span class="hd-l"><img src="'+comment.userImg+'"/></span>'+
                   '<span class="hd-m"><i>'+comment.userName+'</i></span>'+
                   '<span class="hd-r"><i>'+comment.createAt+'</i></span></div>'+
                   '<div class="md"><p></p><span>'+comment.content+'</span>'+
                   '<div class="clearfix">';
                       if(null!=comment.picture&&""!=comment.picture){

                           var pictureList = eval(comment.picture);
                           for(var o in pictureList){
                              html+='<img src="'+pictureList[o]+'">';
                           }
                       }
        html+='</div></div><div class="bm">购买日期: <i>'+comment.buyAt+'</i></div></li>';
     }

    $("#commentUl"+commentType).append(html);
};

//评论分页
function commentPage(page,commentType){
    var skuType=$("#skuType").val();
    var skuTypeId=$("#skuTypeId").val();
    console.log("commentType="+commentType+",page="+page);
      $.ajax({
           type :"GET",
           url : "/comment/detail/"+skuType+"/"+skuTypeId+"/"+page+"/"+commentType,
           contentType: "application/json; charset=utf-8",
           error : function(request) {
               tip("删除失败!");
           },
           success: function(data) {
                if(data.message.code==200){ //成功
                    $("#curPage"+commentType).val(page);
                    if(page==1){
                        if(null==data.count_num||typeof(data.count_num)=="undefined"){
                            $("#commentNumCss"+commentType).html(0);
                        }else{
                            $("#commentNumCss"+commentType).html(data.count_num);
                        }
                         if(null==data.page_count||typeof(data.page_count)=="undefined"){
                            $("#pageCount"+commentType).val(0);
                         }else{
                            $("#pageCount"+commentType).val(data.page_count);
                         }
                    }


                    var commentList = eval(data.remarkList);
                    if(null==commentList||commentList.length<=0){
                       // tip("数据加载完毕");
                        return true;
                    }
                    for(var o in commentList){
                        paintComment(commentList[o],commentType);
                     }

                } else{
                    tip(data.message.message);
                }
           }
      });
};

//提示
function tip(tipContent){
    $("#tip").html(tipContent).show();
    setTimeout(function(){
    $("#tip").hide();
    },3000);
}
//去评价
function commentView(orderId,skuType,skuTypeId,invImg){
    var form = $('<form action="/comment/view" method="post">' +
        '<input type="hidden" name="orderId" value="' + orderId + '" />' +
        '<input type="hidden" name="invImg" value="' + invImg + '" />' +
        '<input type="hidden" name="skuType" value="' + skuType + '" />' +
        '<input type="hidden" name="skuTypeId" value="' + skuTypeId + '" />' +
        '</form>');
        form.submit();
}

//提交评论
$(document).on("click", ".commentBtnCss", function() {
    var content = $("#content").val();
    if (content==""||content.length<10||content.length>500) {
        tip("亲,评论内容需在10~500字以内");
    }  else {
        $.ajax({
            type: "POST",
            url: "/comment/add",
            data: $('form#commentForm').serialize(),
            success: function(data) {
                if (data.code==200) {
                    window.location.href = "/all";
                }
                else{
                    tip(data.message);
                }
            }

        });
    }
});


//上传图片
$(document).on('change','.face',function() {
    var file = $(this);
  //  file.after(file.clone().val(""));
  //  file.remove();
    var files = this.files;
    previewImage(this.files[0],this.value);
});

function previewImage(file,path) {
    var imageType = /image.*/;
    if (!file.type.match(imageType)) {
        throw "File Type must be an image";
    }
    var gallery = document.getElementById("gallery");
    var span = document.createElement("span");
    var img = document.createElement("img");
    span.classList.add('photo');
    span.appendChild(img);
    gallery.appendChild(span);
    var imgNum=gallery.getElementsByTagName("span").length;
    //图片最多为5张
     $(".upload").hide();
    if (imgNum<5) {
        $(".upload").eq(imgNum).show();
    }
    var reader = new FileReader();
    reader.onload = (function(aImg) {
        return function(e) {
            aImg.src = e.target.result;
        }
    })(img);
    reader.readAsDataURL(file);
}


$(function () {
    $(window).on("scroll",function(){
        var commentType=$("#commentType").val();
        var currentPageCount=$("#curPage"+commentType).val();
        var pageCount =  $("#pageCount"+commentType).val();
        if(currentPageCount < pageCount){
            $minUl = $("#commentUl"+commentType);
            console.log($minUl.height())
            if($minUl.height() <= $(window).scrollTop()+500){
                //当最短的ul的高度比窗口滚出去的高度+浏览器高度大时加载新图片
                commentPage(++currentPageCount,commentType);
             //   currentPageCount = currentPageCount + 1;
            }
        }
    })
    $(document).on("click","#commentUl4 img",function () {
        $(".previ").show();
        // $(this).clone().appendTo(".previ").css({
        //     "position":"absolute",
        //     "top":"50%",
        //     "left":0,
        //     "marginTop":-$(".previ").find("img").height()/2,
        // });
        $("#commentUl4 img").each(function () {
            var li = $("<li>");
            $(this).clone().appendTo(li);
            li.appendTo(".bd ul");
        })
        $("#slideBox").css({
            "marginTop":"50%"
        })
        TouchSlide({
            slideCell:"#slideBox",
            titCell:".hd ul", //开启自动分页 autoPage:true ，此时设置 titCell 为导航元素包裹层
            mainCell:".bd ul",
            effect:"leftLoop",
            autoPage:true,//自动分页
        });
    });
    $(".previ").click(function () {
        $(this).find("img").parent().remove();
        $(this).hide();
    })
})



