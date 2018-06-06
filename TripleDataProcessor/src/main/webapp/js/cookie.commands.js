function setCookie(value) {
    var expires = "";
    // if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (9999999999));
        expires = "; expires=" + date.toUTCString();
    // }
    document.cookie = "InputString=" + (value || "")  + expires + "; path=/";
}
function getCookie() {
    var nameEQ = "InputString=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function eraseCookie() {   
    document.cookie = 'InputString=; Max-Age=-99999999;';  
}