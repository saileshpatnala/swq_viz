(function($) {
	$.widget("oclc.viafautox", $.oclc.viafauto, {
		_setOptions: function( options  ) { this._super( options  ); },
		_setOption:  function(key, value) { this._super(key, value); },
		_create:     function(          ) {    return this._super(); },
		_init:       function() {
			var me = this;
			$(me.element).on("viafautoxchange", function(event, ui) {
				$(me.element).removeClass("ui-autocomplete-loading");
				if (! ui.item){
				// console.log("_triggering noselect"); console.log(event.currentTarget);
					me._trigger("noselect", event, {});
				}
			});
		}
	});
})(jQuery);