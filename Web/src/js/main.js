$( document ).ready(function(){
	firebase.auth().onAuthStateChanged(function(user) {
	  if (user) {
	    $("#username").text("Logged in:");
	  } else {
	    $("#username").text("Logged out:");
	  }
	});
	$("#postQuestion").hide();
	showActive();
	$('#selectType').on('change', function() {
  		selectType();
	});
});

var database = firebase.database();
var optCounter=2;
var key;

function selectType(){
	$("#options").empty();
	$("#questionWidget").empty();
	$("#questionWidget").append('<textarea id="questionBox" rows="4" cols="50"></textarea>');
	$("#options").append('<select id="category">'+
	'<option value="Education">Education</option>'+
	'<option value="Politics">Politics</option>'+
	'<option value="Sport">Sport</option>'+
	'<option value="Food">Food</option>'+
	'<option value="Leisure">Leisure</option>'+
	'<select><br/>');
	if($("#selectType").val()=="multiple_choice"){
		$("#options").append('<button onclick="addOption()" id="addButton">Add Option</button>');
		$("#options").append('<div id="divOp1"><input type="text" id="opt1" size="60"><a href="#" id="1" onclick="removeOption(this.parentNode.id)">delete</a></div>');
		
		
	}else if($("#selectType").val()=="rating"){		
		$('#options').append('<h3>Select number of stars for question rating</h3>');
		var noOfStars = '<select id="noOfStars">';
		for(var i=1;i<11;i++){
			noOfStars += '<option value="'+i+'">'+i+'</option>';
		}
		noOfStars += '<select>';
		$('#options').append(noOfStars);
	}
	
	setTimer();
}

function showActive(){
	$("#showQuestion").text("");
	var activeRef = firebase.database().ref('question_details/');
	
	activeRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot) {
			getQuestionDetails(childSnapshot.getKey(),childSnapshot.val().type);
		});	
	});	
}

function getQuestionDetails(key, type){
	var questions;
	questions = '<table style="width:100%"><col width="120"><col width="90">';
	var multipleRef = firebase.database().ref('question_details/'+key);
	multipleRef.on('value', function(snapshot) {
		var childData = snapshot.val().question;
		var theOptions = snapshot.val().options;
		questions += '<tr><td>'+'Posted by '+snapshot.val().postedBy+'</td></tr>';
		questions += '<tr><td style="font-size: 1.5em;">'+childData+'</td><td>';
		
		if(type=="multiple_choice"){
			theOptions.forEach(function(o){
				questions += o + '<br/>';
			});
			questions += '<button type="button" id="analyse" onclick="pieChart(\''+ snapshot.getKey() + '\')">Pie</button>'+
			'<button type="button" id="analyse" onclick="barChart(\''+ snapshot.getKey() + '\')">Bar</button>';
		}else if(type=="rating"){
			for(var i=0;i<snapshot.val().stars;i++){
				questions += i+1+', ';
			}
			questions += '<br><button type="button" id="analyse" onclick="ratingsChart(\''+ snapshot.getKey() + '\')">Ratings</button>';
		}else if(type=="openText"){
			questions += '<button type="button" id="analyse" onclick="openList(\''+ snapshot.getKey() + '\')">Show answers</button>';
		}
		questions += '</tr>'+
		'<td><button type="button" id="removeQuestion" onclick="removeQuestion(\''+ snapshot.getKey() + '\')">DELETE</button></td>';
		questions += '</table>';
		
		$("#showQuestion").append(questions);	
	});
}

function getPreferences(key){
	alert("prefs "+key);
	var questions;
	questions = '<table style="width:100%"><col width="120"><col width="90">';
	var multipleRef = firebase.database().ref('question_details/'+key);
	multipleRef.on('value', function(snapshot) {
		var childData = snapshot.val().question;
		questions += '<tr><td>'+childData+'</td><td>';
		
		for(i=0;i<5;i++){
			questions += ("vote"+i) + '<br/>';
		}
		questions += '<input type="button" id="analyse" onclick="analyse(\''+ childData + '\')"/>'+
		'</tr>';
		questions += '</table>';
		$("#showQuestion").append(questions);	
	});
}

function addQuestion(){
	var i=0;
	var theoptions=[];
	
	var userId = firebase.database().ref().child('question_details').push().key;
	var currDate = new Date().getDate();
	var currDay = new Date().getDay();
	var currHours = new Date().getHours();
	var currMinutes = new Date().getMinutes();
	var currMonth = new Date().getMonth();
	var currSeconds = new Date().getSeconds();
	var currTime = new Date().getTime();
	var offset = new Date().getTimezoneOffset();
	var currYear = new Date().getYear();
	
	var theTimestamp={
		date : currDate,
		hours : currHours,
		minutes : currMinutes,
		month : currMonth,
		seconds : currSeconds,
		time : currTime,
		timezoneOffset : offset,
		year : currYear
	};
	
	
	
	var deadlineMinutes = currMinutes + Number ($("#minutes").val());
	var deadlineHours = currHours + Number($("#hours").val()) + Math.trunc(deadlineMinutes/60);
	var deadlineDate = currDate + (Number($("#days").val())) + Math.trunc(deadlineHours/24);
	var deadlineMonth = currMonth;
	if(currMonth==0 || currMonth==2 || currMonth==4 || currMonth==6 || currMonth==7 || currMonth==9 || currMonth==11 || currMonth==12){
		if(deadlineDate>31){
			deadlineMonth += 1;
		}
		deadlineDate %= 31;		
	}else if(currMonth==3 || currMonth==5 || currMonth==8 || currMonth==10){
		if(deadlineDate>30){
			deadlineMonth += 1;
		}
		deadlineDate %= 30;		
	}else if(currMonth==1){
		if(deadlineDate>28){
			deadlineMonth += 1;
		}
		deadlineDate %= 28;
	}
	var deadlineTime = currTime + $("#minutes").val()*60000 + $("#hours").val()*60000*60 + $("#days").val()*60000*60*24;
	
	var theDeadline={
		date : deadlineDate,
		hours : deadlineHours % 24,
		minutes : deadlineMinutes % 60,
		month : deadlineMonth,
		time : deadlineTime,
		timezoneOffset : offset,
		year : currYear
	};

   $('#options').find('div').each(function(){
	    var innerDivId = $(this).attr('id');
	    $('#'+innerDivId+' input[type=text]').each(function (){
	    	theoptions.push($(this).val());
	    });
	});

	if($("#questionBox").val().length<1){
		alert("Please enter a question");
	}else if(($("#minutes").val()<1 || $("#minutes").val()>59) && $("#hours").val()<1 && $("#days").val()<1){
		alert("Invalid time limit");
	}else if($("#selectType").val()=="multiple_choice"){
		if(optCounter<3){
			alert("A question must have at least two options");
		}else{
			if(validateOptions()=="true"){
				firebase.database().ref('question_details/' + userId).set({
				"question" : $("#questionBox").val(),
				"postedBy" : firebase.auth().currentUser.email,
				"type" : $("#selectType").val(),
				"category" : $("#category").val(),
				"options" : theoptions,
				"timeStamp" : theTimestamp,
				"deadline" : theDeadline
			});
			$('#options').empty();
			}else{
				alert("Please enter options");
			}
			
		
		}	
	}else if($("#selectType").val()=="rating"){
		firebase.database().ref('question_details/' + userId).set({
			"question" : $("#questionBox").val(),
			"postedBy" : firebase.auth().currentUser.email,
			"type" : $("#selectType").val(),
			"category" : $("#category").val(),
			"stars" : $("#noOfStars").val(),
			"timeStamp" : theTimestamp,
			"deadline" : theDeadline
		});
	}else if($("#selectType").val()=="openText"){
		firebase.database().ref('question_details/' + userId).set({
			"question" : $("#questionBox").val(),
			"postedBy" : firebase.auth().currentUser.email,
			"type" : $("#selectType").val(),
			"category" : $("#category").val(),
			"timeStamp" : theTimestamp,
			"deadline" : theDeadline
		});
  	}
}

function addOption(){
	$("#options").append('<div id="divOp'+optCounter+'"><label for="opt'+optCounter+'"></label><input type="text" size="60" id="opt'+optCounter+'"><a href="#" id="opt'+optCounter+'" onclick="removeOption(this.parentNode.id)">delete</a></div>');
	//$('label[for="opt'+ optCounter +'"]').text("opt"+optCounter);
	optCounter++;
	theoptions=[];
}

function removeOption(theID){
	var newOption = document.getElementById(theID);
	newOption.parentNode.removeChild(newOption);
	optCounter--;
}

function setTimer(){
	$("#setTime").empty();
	$("#setTime").append('<h2>Set the time limit here.</h2>');
	$("#setTime").append('<label for = "days">Days</label>');
	$("#setTime").append('<input id="days" type="text" placeholder="0">');
	$("#setTime").append('<label for = "days">Hours</label>');
	$("#setTime").append('<input id="hours" type="text" placeholder="0-23">');
	$("#setTime").append('<label for = "days">Minutes</label>');
	$("#setTime").append('<input id="minutes" type="text" placeholder="0-59">');
	$("#postQuestion").show();
}

function removeQuestion(userId){
	var x;
    if (confirm("Delete question. Are you sure?") == true) {
        firebase.database().ref('question_details/' + userId).remove();
        firebase.database().ref('answers/' + userId).remove();
		showActive();
    }
}

function validateOptions(){
	var valid="true";
	$('#options').find('div').each(function(){
	    var innerDivId = $(this).attr('id');
	    $('#'+innerDivId+' input[type=text]').each(function (){
	    	if($(this).val().length<1){
	    		valid="false";
	    	}
	    });
	});
	return valid;
}
