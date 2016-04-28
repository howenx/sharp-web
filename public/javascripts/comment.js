//点击评论页签
function commentTab(skuType,skuTypeId,commentType){
     var index=commentType-1;
     sessionindex = index;
     $('.scroll-wrap .scroll-content section').eq(index).show().siblings().hide();
     $('.nav-tab-top li').find("a").removeClass('current');
     $('.nav-tab-top li').eq(index).find("a").addClass('current');
    // $(this).addClass('current').parent().siblings().children('a').removeClass('current');
    var curPage=$("#curPage"+commentType).val();
    if(curPage>0){ //说明请求过了,只切换当前状态
      return false;
    }
    $.ajax({
           type :"GET",
           url : "/comment/detail/"+skuType+"/"+skuTypeId+"/1"+"/"+commentType,
           contentType: "application/json; charset=utf-8",
           error : function(request) {
               tip("获取数据失败,请重新尝试!");
           },
           success: function(data) {
                if(data.message.code==200){ //成功
                    $("#curPage"+commentType).val(1);
                    var commentList = eval(data.remarkList);
                    for(var o in commentList){
                        paintComment(commentList[o],commentType);
                     }

                } else{
                    tip(data.message.message);
                }
           }
      });
};

//绘制评论
function paintComment(comment,commentType){
    var html='<li><div class="hd clearfix">'+
            '<span class="hd-l"><img src="'+comment.userImg+'"/></span>'+
            '<span class="hd-m"><i>'+comment.userName+'</i></span>'+
            '<span class="hd-r"><i>'+comment.createAt+'</i></span></div>'+
            '<div class="md"><p></p><span>'+comment.content+'</span>'+
            '<div class="clearfix">';

            if(null!=comment.picture&&""!=comment.picture){
                if(commentType==4){
                    html+='<img src="'+comment.picture+'">';

                }else{
                    var pictureList = eval(comment.picture);
                    for(var o in pictureList){
                       html+='<img src="'+pictureList[o]+'">';
                    }
                }
            }
       html+='</div></div></li>';
       $("#commentUl"+commentType).append(html);
};

//评论分页
function commentPage(page,commentType){
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
                    var commentList = eval(data.remarkList);
                    if(null==commentList||commentList.size()<=0){
                        tip("暂无最新数据");
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
    if (content==""||content.length<5||content.length>500) {
        tip("亲,评论内容需在5~500字以内");
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
    file.after(file.clone().val(""));
    file.remove();
    var files = this.files;
    previewImage(this.files[0]);
});

function previewImage(file) {
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
    gallery.appendChild(span);
    //图片最多为5张
    if (gallery.getElementsByTagName("span").length==5) {
        $(".upload").css("display","none");
    }
    var reader = new FileReader();
    reader.onload = (function(aImg) {
        return function(e) {
            aImg.src = e.target.result;
        }
    })(img);
    reader.readAsDataURL(file);
}
var sessionindex = 0;
$(function () {
    var currentPageCount = 2;
    $(window).on("scroll",function(){
        console.log(sessionindex);
        var pageCount = 4;
        if(currentPageCount <= pageCount){
            $minUl = $("section").eq(sessionindex);
            if($minUl.height() <= $(window).scrollTop()+$(window).height()){
                //当最短的ul的高度比窗口滚出去的高度+浏览器高度大时加载新图片
                paintComment(currentPageCount);
                currentPageCount = currentPageCount + 1;
            }
        }
    })
})



