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
					$("#content").scrollTop(0)
				}
			});
		}
	});
	
	$("li.tab").on('click', function(){
		$('div.pointer').remove();
		$("li.tab").removeClass("active");
		$(this).addClass("active");
		$(this).prepend(
			'<div class="pointer">' +
                '<div class="arrow"></div>' +
                '<div class="arrow_border"></div>' +
            '</div>'	
		);
	});
})(jQuery);