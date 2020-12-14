(function () {
    "use strict";
    var loginForm = document.getElementById("login");
    var signupForm = document.getElementById("signup");
    var error = document.getElementById("error");
    
    function reset() {  
    loginForm.hidden = false;
    signupForm.hidden = true;
    error.hidden = true;
    };
    
    //https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript
    function validateEmail(email) {
        const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }
    
    function checkSignup(form) {
        if (form != null) {
            var user = form.get("username");
            var email = form.get("email");
            var psw = form.get("password");
            var conf = form.get("confirm");
            if (user.length>0) {
                if (validateEmail(email)){
                    if(psw.length>0 && psw === conf) {
                        error.hidden = true;
                        return true;
                    }else {
                        error.innerHTML = "Le passwords non corrispondono e devono contenere almeno un carattere."
                        error.hidden = false;
                    }
                }else{
                    error.innerHTML = "L'email non è sintatticamente valida."
                    error.hidden = false;
                }
            }else {
                error.innerHTML = "L'username deve contenere almeno un carattere."
                error.hidden = false;
            }
        }
        return false;
    }
    
    var doLogin = (e) => {
        e.preventDefault();
        console.log("Try Login");
        let form = new FormData(e.target.closest("form"));
        let url = "login";
        sendData("POST",url,form, 
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            sessionStorage.setItem("username",form.get("username"));
                            window.location.href = "homepage.html";
                        } else {
                            error.innerHTML = message;
                            error.hidden = false;
                        }
                    }
                });
    }
    
    var doSignUp = (e) => {
        e.preventDefault();
        console.log("Try Sign Up");
        var form = e.target.closest("form");
        let formData = new FormData(form);
        if (checkSignup(formData)){
            formData.delete("confirm");
            console.log("Check verified!!");
            let url = "signup";
            sendData("POST",url,formData, 
                    function(req) {
                        if (req.readyState == 4) {
                            var message = req.responseText;
                            if (req.status == 201) {
                            	form.reset();
                                reset();
                            } else {
                                error.innerHTML = message;
                                error.hidden = false;
                            }
                        }
                    });
        }
    }
    

    //LOGIN FORM
    var loginBut = document.getElementById("loginButton");
    var signupLink = document.getElementById("signupLink");
    
    
    loginBut.onclick = doLogin;
    
    signupLink.addEventListener("click", (e) => {
        e.preventDefault();
        loginForm.hidden = true;
        signupForm.hidden = false; 
    });
    
    //SIGN UP FORM
    var backBut = document.querySelector("#backButton");
    var signupBut = document.querySelector("#signupButton");
    
    backBut.addEventListener("click",(e) => {
        e.preventDefault();
        reset();
    });
    signupBut.onclick = doSignUp;
    
    //Handle the enter key in the sign up form
    for (var input of document.querySelectorAll("#signup input")){
        input.onkeydown = (e) => {
            if (e.keyCode === 13) {
                e.preventDefault();
                document.getElementById("signupButton").click();
            }
        };
    }
})();