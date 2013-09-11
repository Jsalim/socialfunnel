
var tabsTimer;

function helpdeskTabResizeCheck(){
	
	console.log("finish - " + $("#ticket-tabs").width());
	
	if($("#ticket-tabs").width() / $(".helpdesk-tabs").width() > 0.75){
		var elem = $("#ticket-tabs li.visible-tab").last();
		$(elem).removeClass("visible-tab")
		$(elem).addClass("hidden-tab");
	}else{
		var elem = $("#ticket-tabs li.hidden-tab").first();
		$(elem).removeClass("hidden-tab");
		$(elem).addClass("visible-tab");
	}
} 


(function($) {
	$("a").on("click", function(e) {
		var source = $(this).attr("data-source");
		var target = $(this).attr("data-target");
		if (source == null)
			source = $(this).attr("href");
		if (source != null && target != null) {
			e.preventDefault();
			$.ajax({
				type : "GET",
				url : source,
				success : function(response) {
					$('div[data-id="' + target + '"]').html(response);
					
					$(document).scrollTop(0)
				}
			});
		}
	});
	
	$("li.tab").on('click', function(){
		$('div#sidebar-nav div.pointer').remove();
		$("li.tab").removeClass("active");
		$(this).addClass("active");
		$(this).prepend(
			'<div class="pointer">' +
                '<div class="arrow"></div>' +
                '<div class="arrow_border"></div>' +
            '</div>'	
		);
	});
	
	$(window).resize(function() {
		console.log("start - " + $("#ticket-tabs").width());
	    clearTimeout(tabsTimer);
	    tabsTimer = setTimeout(helpdeskTabResizeCheck, 100);
	});

	
})(jQuery);