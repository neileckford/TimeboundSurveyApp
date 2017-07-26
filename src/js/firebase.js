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
	var email = $("#txtEmail").val();
	var pass = $("#txtPassword").val();
	var auth = firebase.auth();
	firebase.auth().createUserWithEmailAndPassword(email, pass).catch(function(error) {	
  		var errorCode = error.code;
  		var errorMessage = error.message;
});
}