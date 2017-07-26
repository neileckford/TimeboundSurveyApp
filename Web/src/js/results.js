function analyse(quesID){
	getResponseTime(quesID);
}

function showAnalysis(key){
	var results;
	var title;
	var theOptions=[];
	var choices=[];
	
	var optionRef = database.ref('question_details/'+key);
	optionRef.on('value', function(snapshot) {
		theOptions = snapshot.val().options;
		title = snapshot.val().question;
	});
	
	var multipleRef = database.ref('question_details/'+key);
	multipleRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			choices.push(0);
		});
	});
	
	results = '<table style="width:100%"><col width="120"><col width="90">';
	var resultsRef = database.ref('answers/'+key);
	resultsRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			//alert(childSnapshot.val().user+" "+childSnapshot.val().answer);
			if(childSnapshot.val().answer==theOptions[0])
				choices[0]++;
			if(childSnapshot.val().answer==theOptions[1])
				choices[1]++;
			if(childSnapshot.val().answer==theOptions[2])
				choices[2]++;
		});
		//alert(theOptions[0]+" "+choices[0]+"No: "+choices[1]+"Undecided: "+choices[2]);
		var chart = new CanvasJS.Chart("chartContainer",
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
		theme: "theme1",
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
			dataPoints: [
				{  y: choices[0], name: theOptions[0], legendMarkerType: "triangle"},
				{  y: choices[1], name: theOptions[1], legendMarkerType: "square"},
				{  y: choices[2], name: theOptions[2], legendMarkerType: "circle"}
			]
		}
		]
	});
	chart.render();
	});	
}

function pieChart(key){
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
				pieChart.push ({y: choices[i], name: theOptions[i], legendMarkerType: "circle"});
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

	var optionRef = database.ref('question_details/'+key);
	optionRef.on('value', function(snapshot) {
		theOptions = snapshot.val().options;
		title = snapshot.val().question;
		for(var i=0;i<theOptions.length;i++){
			choices.push(0);
		}
	});
	
	var resultsRef = database.ref('answers/'+key);
	resultsRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			for(var i=0;i<theOptions.length;i++){
				if(childSnapshot.val().answer==theOptions[i]){
				choices[i]++;
			}
			}
		});

		var barChart = [];
		for(var i=0;i<theOptions.length;i++){
			if(choices[i]>0)
				barChart.push ({label: theOptions[i], y: choices[i]});
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
}

function ratingsChart(key){
	var chartTitle;
	var scale;
	var ratings = [];
	var totalResponses=0;
	var axis=[];
	var totalRating=0;
	
	var questionRef = database.ref('question_details/'+key);
	questionRef.on('value', function(snapshot) {
		chartTitle = snapshot.val().question;
		scale = snapshot.val().stars;
		for(var i=0;i<scale;i++)
			ratings.push(0);
	});
	
	var ratingsRef = database.ref('answers/'+key);
	ratingsRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			ratings[childSnapshot.val().answer-1]+=1;	
			totalResponses+=1;
		});

	for(var i=1;i<=scale;i++){
		axis.push({y: ratings[i-1], label: i});
		totalRating+=(ratings[i-1]*i);
	}
	axis.push({y: totalRating/totalResponses, label: "Average"});
	
	var chart = new CanvasJS.Chart("analysis", {
		title:{
			text: chartTitle				
			},
			animationEnabled: true,
			axisX:{
				interval: 1,
				gridThickness: 0,
				labelFontSize: 10,
				labelFontStyle: "normal",
				labelFontWeight: "normal",
				labelFontFamily: "Lucida Sans Unicode"

			},
			axisY2:{
				interlacedColor: "rgba(1,77,101,.2)",
				gridColor: "rgba(1,77,101,.1)"

			},
			
			data: [
			{     
				type: "bar",
                name: "ratings",
				axisYType: "secondary",
				color: "#FFD700",				
				dataPoints: axis
			}
			
			]
		});

chart.render();
});
}

function openList(key){
	//$("#analysis").empty();
	var openList = "";
	var titleRef = database.ref('question_details/'+key);
	titleRef.on('value', function(snapshot) {
		openList += '<h2>'+snapshot.val().question+'</h2>';
	});
	openList += '<br><br>';
	var answerRef = database.ref('answers/'+key);
	
	openList += '<table style="background-color: #ffffff">';
	answerRef.on('value', function(snapshot) {
		snapshot.forEach(function(childSnapshot){
			openList += '<tr><td>'+childSnapshot.val().user+'</td><td>'+childSnapshot.val().answer+'</td></tr>';
		});
		openList += '</table>';
		$("#analysis").append(openList);
	});
	
}
