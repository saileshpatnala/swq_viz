$(function() {
	$("#myViafId").viafautox( {
		select: function(event, ui){
			var item = ui.item;
			var message = item.id;
			post(message);
			event.preventDefault();
			event.stopPropagation();
			$(this).val(item.value);
		} 
	});
});

