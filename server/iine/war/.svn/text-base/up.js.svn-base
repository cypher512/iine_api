
function createhttprequest() {
	var request = null;
	if ("XMLHttpRequest" in window) {
		request = new XMLHttpRequest();
	} else if ("ActiveXObject" in window) {
		try {
			request = new ActiveXobject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {
			}
		}
	}
	return request;
}

var request;

function requestsorce() {
	request = createhttprequest();
	request.open("GET", "http://www.112api.jp/u", true);
	request.onreadystatechange = res;
	request.send("");
}

function res() {
	if (request.readyState == 4 && request.status == 200) {
		alert(request.responseText);
	} else {
	}
}
