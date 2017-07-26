function signin(){
	var email = $("#txtEmail").val();
	var pass = $("#txtPassword").val();
	var auth = firebase.auth();
	var promise = auth.signInWithEmailAndPassword(email,pass);
	var user = firebase.auth().currentUser;
}

function signout(){
	firebase.auth().signOut();
	console.log("Signed out");
}

function register(){
	var email = $("#newEmail").val();
	var pass = $("#newPassword").val();
	var confirm = $("#confirmPassword").val();
	var auth = firebase.auth();
	var valid = "false";
	
	if(email.includes("@"))
			var valid = "true";
			
	if(email.length<6 || valid=="false"){
		alert("Please enter valid email");
	}else if(pass.length<8){
		alert("Passwords must be at least 8 characters");
	}else if(pass==confirm){
		firebase.auth().createUserWithEmailAndPassword(email, pass).catch(function(error) {	
  		var errorCode = error.code;
  		var errorMessage = error.message;
  		
  		alert("You are now registered as "+email);
		});
	}else{
		alert("confirm password does not match");
	}
}