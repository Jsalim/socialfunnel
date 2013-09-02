function checkWidth(){
	if($(window).width() <= 1020 && $(window).width() > 767){
		$("#dashboard-menu .tab a span").css("opacity", 0);
		$("#sidebar-nav").css("width", 65);
		$(".content").css("margin-left", 62);
		$("#sidebar-nav div.pointer").css("opacity", 1);
		$(".resizer-icon").removeClass("small");
		$(".resizer-icon").addClass("small");
	}else if($(window).width() <= 767){
		$("#sidebar-nav div.pointer").css("opacity", 0);
		$(".content").css("margin-left", 0);
	}else if($(window).width() > 1020){
		$("#sidebar-nav div.pointer").css("opacity", 1);
		if($(".resizer-icon").hasClass("small")){
			$("#dashboard-menu .tab a span").css("opacity", 0);
			$("#sidebar-nav").css("width", 65);
			$(".content").css("margin-left", 62);
		}else{
			$("#dashboard-menu .tab a span").css("opacity", 1);
			$("#sidebar-nav").css("width", 180);
			$(".content").css("margin-left", 177);
		}
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
	
	$(".resizer-icon").on('click', function(){
		if($(this).hasClass("small") == false){
			$("#dashboard-menu .tab a span").css("opacity", 0);
			$("#sidebar-nav").css("width", 65);
			$(".content").css("margin-left", 62);
			$(this).addClass("small");
		}else{
			$("#dashboard-menu .tab a span").css("opacity", 1);
			$("#sidebar-nav").css("width", 180);
			$(".content").css("margin-left", 177);
			$(this).removeClass("small");
		}
	});
	
	$(window).resize(function() {
		checkWidth();
	});
	
	checkWidth();
	
})(jQuery);