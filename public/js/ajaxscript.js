
function helpdeskTabSwitch(){
	
	$(".contact-tab").on('click', function(){
		$(this).parent().children("li").removeClass("active");
		$(this).addClass("active");
	});
}

(function($) {
	
	$("#sidebar-nav li").on("click", function(e){
		var tab = ($(this));
		var anchor = ($(this).children("a"));
		
		if(anchor){
			var source = $(anchor).attr("data-source");
			var target = $(anchor).attr("data-target");
			if (source == null)
				source = $(anchor).attr("href");
			if (source != null && target != null) {
				e.preventDefault();
				$.ajax({
					type : "GET",
					url : source,
					success : function(response) {
						$('div[data-id="' + target + '"]').html(response);
						
						$('div#sidebar-nav div.pointer').remove();
						$("li.tab").removeClass("active");
						$(tab).addClass("active");
						$(tab).prepend(
							'<div class="pointer">' +
				                '<div class="arrow"></div>' +
				                '<div class="arrow_border"></div>' +
				            '</div>'	
						);
						
						$(document).scrollTop(0)
					}
				});
			}
		}
	});
	
	helpdeskTabSwitch();
	
})(jQuery);