window.restURL = "rest/";

$(document).ajaxStart(function() {
	$('#spinner').show();
}).ajaxStop(function() {
	$('#spinner').hide();
});

function showSuccess(msg) {
    alert(msg);
}

function showFailure(xhr, status, error) {
	var errmsg = "The following errors occurred:\n\n";
	$.each(xhr.responseJSON, function(key, value) {
		errmsg += value.message + "\n";
	})
	alert(errmsg)
    if ( window.console && window.console.log ) {
    	console.log("Response JSON:\n" + JSON.stringify(xhr.responseJSON));
    	console.log("XMLHttpRequest:\n" + xhr);
    	console.log("Status: " + status);
    	console.log("Error object: " + error);
    }
}
