$(function(){
    $('.account-j h2').click(function(){
        $(this).parents(".account-j").find(".way").toggle();
    })

    $('li').click(function(){
        $(this).parents("ul").prev().find(".quick").html($(this).find(".quick").html())
    })

    $('.back').click(function(){
        $('.shade').show();
    })

    $('.first').click(function(){
        $('.shade').hide();
    })

})

