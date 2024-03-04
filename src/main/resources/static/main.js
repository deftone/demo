$(document).ready(function() {

	//dropdown
	$("nav li:has(ul)").hover(function(){
		$(this).find("ul").slideDown();
	}, function(){
		$(this).find("ul").hide();
	});

	// menu for small devices
	$('.menu_button').on('click', function(){
		$('.menu_items').toggleClass('hidden');
	});
	$('.menu_items li').click(function(){
		$('.menu_items').addClass('hidden');
	});

});

// $(function(){
// var overlay = $('<div id="overlay"></div>');
// overlay.show();
// overlay.appendTo(document.body);
// $('.popup').show();
// $('.close').click(function(){
// $('.popup').hide();
// overlay.appendTo(document.body).remove();
// return false;
// });




$('.x').click(function(){
$('.popup').hide();
overlay.appendTo(document.body).remove();
return false;
});
});