(function($) {

	$.widget("oclc.viafautox", $.ui.autocomplete, {
	 options: {
   // select: function(event, ui) { alert("Selected!"); return this._super(event, ui); },
	  source: function(request, response) {
		  var term = $.trim(request.term); 
		  var url  = "http://viaf.org/viaf/AutoSuggest?query=" + term;
		  var me = this; 
		  $.ajax({
			  url: url,
			  dataType: "jsonp",
			  success: function(data) {
				  if (data.result) {
					  response( $.map( data.result, function(item) {
						  var retLbl = item.term + " [" + item.nametype + "]";
						  return {
							  label: retLbl,
							  value: item.term,
							  id: item.viafid,
							  nametype: item.nametype
						  }
					  }));
				  } else {
					  me._trigger('nomatch', null, {term: term});
				  }
			  },
		  });  // end of $.ajax()
	  }},      // end of source:, options

	  /*
	   * Punt a few fundamental tasks to the parent class
	   */
	  _create: function() {
		  return this._super();
	  },
	  _setOption: function( key, value ) {
		  this._super( key, value );
	  },
	  _setOptions: function( options ) {
		  this._super( options );
	  }
	});
})(jQuery);
