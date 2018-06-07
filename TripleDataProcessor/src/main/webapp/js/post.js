function post( message ) {
	$( "#submit" ).click(function() {
	  $.ajax({
		type: "POST",
		url: 'http://localhost:8080/TripleDataProcessor/webapi/myresource',
		data: message,
		success: function(response) { 
		  console.log('POST successful');
		  delete_cookie('InputString');
		  SetCookie('InputString', document.getElementById('myViafId').value);
		  window.location = response;
		},
		contentType: 'text/plain',
	  });

	}); 
}

