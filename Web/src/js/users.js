$( document ).ready(function(){
	users();
	
});

var sum=0;
var totalResponse=[];
var allUsers=[];

function users(){
	var userRef = database.ref('respondents/');
	userRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			userAnalysis(childSnapshot.val().userName);
		});
	});
	
}

function userAnalysis(user){
	var multipleRef = database.ref('answers/');
	multipleRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			var answerRef = database.ref('answers/'+childSnapshot.getKey());
			answerRef.on('value', function(snapshot) {
				snapshot.forEach(function(childSnapshot){
					if(childSnapshot.val().user==user){
						totalResponse.push(childSnapshot.val().responseTime);
					}
				});
				
			});
			
		});
		
		
		for(var i=0;i<totalResponse.length;i++){
			sum += totalResponse[i];
			//alert(user+" "+totalResponse[i]);
		}
		allUsers.push({y: sum/totalResponse.length/1000,  label: user});
		//reset average variables
		totalResponse=[];
		sum=0;
		
		var chart = new CanvasJS.Chart("responseContainer",
	    {
	      title:{
	        text: "Average Response Times"   
	      },
	      animationEnabled: true,
	      axisY: {
	        title: "Response Time (Seconds)"
	      },
	      legend: {
	        verticalAlign: "bottom",
	        horizontalAlign: "center"
	      },
	      theme: "theme2",
	      data: [
	
	      {        
	        type: "column",  
	        showInLegend: true, 
	        legendMarkerColor: "grey",
	        legendText: "Average response time",
	        //dataPoints: [{y: sum/totalResponse.length,  label: user}]
	        dataPoints: allUsers
	      }   
	      ]
	    });
	
	  chart.render();
	});
}

/*function getResponseTime(key){
	var results;
	var title;
	var theOptions=[];
	var choices=[];
	var responseTime=[];
	
	
	var optionRef = database.ref('question_details/'+key);
	optionRef.on('value', function(snapshot) {
		theOptions = snapshot.val().options;
		title = snapshot.val().question;
		for(var i=0;i<theOptions.length;i++){
			responseTime.push(0);
			choices.push(0);
		}
	});
	
	results = '<table style="width:100%"><col width="120"><col width="90">';
	var resultsRef = database.ref('answers/'+key);
	resultsRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			for(var i=0;i<theOptions.length;i++){
				if(childSnapshot.val().answer==theOptions[i]){
				choices[i]++;
				responseTime[i]+=childSnapshot.val().responseTime;
			}
			}
		});

		var pieChart = [];
		for(var i=0;i<theOptions.length;i++){
			if(choices[i]>0)
				pieChart.push ({y: responseTime[i]/choices[i], name: theOptions[i], legendMarkerType: "circle"});
		}

	var chart = new CanvasJS.Chart("analysis",
	{
		title:{
			text: title,
			fontFamily: "arial black"
		},
                animationEnabled: true,
		legend: {
			verticalAlign: "bottom",
			horizontalAlign: "center"
		},
		theme: "theme2",
		data: [
			{        
				type: "pie",
				indexLabelFontFamily: "Garamond",       
				indexLabelFontSize: 20,
				indexLabelFontWeight: "bold",
				startAngle:0,
				indexLabelFontColor: "MistyRose",       
				indexLabelLineColor: "darkgrey", 
				indexLabelPlacement: "inside", 
				toolTipContent: "{name}: {y}",
				showInLegend: true,
				indexLabel: "#percent%", 
				dataPoints: 
					pieChart	
			}
		]
	});
	chart.render();
	});
}

function barChart(key){
	var results;
	var title;
	var theOptions=[];
	var choices=[];
	var responseTime=[];
	
	
	var optionRef = database.ref('question_details/'+key);
	optionRef.on('value', function(snapshot) {
		theOptions = snapshot.val().options;
		title = snapshot.val().question;
		for(var i=0;i<theOptions.length;i++){
			responseTime.push(0);
			choices.push(0);
		}
	});
	
	var resultsRef = database.ref('answers/'+key);
	resultsRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			for(var i=0;i<theOptions.length;i++){
				if(childSnapshot.val().answer==theOptions[i]){
				choices[i]++;
				responseTime[i]+=childSnapshot.val().responseTime;
			}
			}
		});

		var barChart = [];
		for(var i=0;i<theOptions.length;i++){
			if(choices[i]>0)
				barChart.push ({label: theOptions[i], y: responseTime[i]/choices[i]});
		}
	var chart = new CanvasJS.Chart("analysis", {
		theme: "theme2",
		title:{
			text: title             
		},
		animationEnabled: true,
		data: [              
		{
			type: "column",
			dataPoints: barChart
		}
		]
	});
	chart.render();
	});
}*/