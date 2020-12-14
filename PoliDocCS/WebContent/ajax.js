function sendData(method, url, formObj, callback) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
      callback(req)
    }; 
    req.open(method, url);
    if (formObj == null) {
      req.send();
    } else {
      req.send(formObj);
    }
}