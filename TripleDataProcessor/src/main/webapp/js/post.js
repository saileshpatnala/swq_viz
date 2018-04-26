function post( message ) {
	$( "#submit" ).click(function() {
	  $.ajax({
		type: "POST",
		url: 'http://localhost:8080/TripleDataProcessor/webapi/myresource',
		data: message,
		success: function(response) { 
		 	FormAggregate(123456789);
		  console.log('POST successful'); 
		  window.location = response;
		},
		contentType: 'text/plain',
	  });

	}); 
}

// expiry set for 1 day
function SetCookie(name, value){
	var expires = "";
    if (true) {
        var date = new Date();
        date.setTime(date.getTime() + (1*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function GetCookie(name) {
	var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
function FormAggregate(viafValue) {
	// var ouput = [];
	// ouput[0] = viafValue;
	// ouput[1] = document.getElementById("ViafIDCheck").checked;
	// ouput[2] = $("input[type=radio][name=CSVValues]:checked").val();
	// ouput[3] = $("input[type=radio][name=DepthRadios]:checked").val();
	// ouput[4] = "" + convertIDBoolean("LibSrc") + convertIDBoolean("ViafSrc") + convertIDBoolean("OCLCSrc");
	// ouput[5] = $("input[type=radio][name=OutputDisplay]:checked").val();

	// return {ouput:ouput};

	// var viaf = viafValue;
	var CSV = $("input[type=radio][name=CSVValues]:checked").val();
	var Depth = $("input[type=radio][name=DepthRadios]:checked").val();
	var SourceOpts = "" + convertIDBoolean("LibSrc") + convertIDBoolean("ViafSrc") + convertIDBoolean("OCLCSrc");
	var SViafID = document.getElementById("ViafIDCheck").checked;
	var Display = $("input[type=radio][name=OutputDisplay]:checked").val();

	SetCookie('viafID', viafValue);
	SetCookie('CSVDownload', CSV);
	SetCookie('GraphDepth', Depth);
	SetCookie('SourceOptions', SourceOpts);
	SetCookie('ShowViafID', SViafID);
	SetCookie('DisplayOutput', Display);

	// console.log("CSV :",CSV);
	// console.log("Depth :",Depth);
	// console.log("SourceOpts :",SourceOpts);
	// console.log("Display :",Display);
}

function convertIDBoolean(ID){
	if(document.getElementById(ID).checked === true)
		return 1;
	return 0;
}