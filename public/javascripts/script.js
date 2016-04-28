//star
$(document).ready(function(){
    var stepW = 34;
    /*var description = new Array("");*/
    var stars = $("#star > li");
    var descriptionTemp;
    function myfn(i){
        if(0<=i&&i<=4){
            n = i+1;
            $("#showb1").css({"width":stepW*n});
        }
        else if(5<=i&&i<=9){
            i = i-5;
            n = i+1;
            $("#showb2").css({"width":stepW*n});
        }
        else if(10<=i&&i<=14){
            i = i -10;
            n = i+1;
            $("#showb3").css({"width":stepW*n});
        }

        $("#grade").val(i+1);
    }
    stars.each(function(i){
        $(stars[i]).click(function(e){
            myfn(i);
            return stopDefault(e);
        });
    });
    stars.each(function(i){
        $(stars[i]).hover(
            function(){
                $(".description").text(description[i]);
            },
            function(){
                if(descriptionTemp != null)
                    $(".description").text("");
                else
                    $(".description").text(" ");
            }
        );
    });
    $('.foot i').click(function(){
        $(this).toggleClass('current');
    })
});
function stopDefault(e){
    if(e && e.preventDefault)
        e.preventDefault();
    else
        window.event.returnValue = false;
    return false;
};







