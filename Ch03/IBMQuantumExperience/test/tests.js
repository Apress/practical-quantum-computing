// https://medium.com/dev-bits/writing-neat-asynchronous-node-js-code-with-promises-32ed3a4fd098

const qx = require('../'); // require the `index.js` file from the same directory.

// Put your API token here
var config = { APItoken: 'API-TOKEN'
	, debug		: true
	, 'url'		: 'https://quantumexperience.ng.bluemix.net/api'
    , 'hub'		: 'MY_HUB'
    , 'group'	: 'MY_GROUP'
    , 'project'	: 'MY_PROJECT'
};

var errHandler = function(err) {
    console.log(err);
}

function testCalibration() {
	// https://medium.com/dev-bits/writing-neat-asynchronous-node-js-code-with-promises-32ed3a4fd098
	var p1 = qx.init(config);
	
	p1.then( result => {
		var p2 = qx.getCalibration('ibmqx4');
		
		p2.then ( result => { 
			console.log("-- BACKEND CALIBRATION ibmqx4 --\n" + JSON.stringify(result) + '\n----------' ); 
			
			var p2 = qx.getParameters('ibmqx4');
			p2.then( result => { console.log("-- BACKEND PARAMETERS ibmqx4 --\n" + JSON.stringify(result) + "\n----------" ) } );
			
		});
	}, errHandler);
}

async function testBackends() {
	/*
	var p1 = qx.init(config);
	
	p1.then( result => {
		var p2 = qx.getBackends();
		
		p2.then ( result => { 
			console.log("---- BACKENDS ----\n" + JSON.stringify(result) + "\n-----" )
		});
		
	}, errHandler); */
	await qx.init(config);
	var result = await qx.getBackends();
	console.log("---- BACKENDS ----\n" + JSON.stringify(result) + "\n-----" );
}

function testExperiment () {
	var p1 = qx.init(config);
	p1.then( result => {
		var name 	= "REST Experiment from Node JS #1"
		var qasm 	=  "\n\ninclude \"qelib1.inc\";\nqreg q[5];\ncreg c[5];\nu2(-4*pi/3,2*pi) q[0];\nu2(-3*pi/2,2*pi) q[0];\nu3(-pi,0,-pi) q[0];\nu3(-pi,0,-pi/2) q[0];\nu2(pi,-pi/2) q[0];\nu3(-pi,0,-pi/2) q[0];\nmeasure q -> c;\n";
		var shots 	= 1;
		var device 	= "simulator"; // "ibmqx4";
		var p2 		= qx.runExperiment(name, qasm, shots, device);
		
		p2.then ( result => { 
			console.log("---- EXPERIMENT " + name + " ----\n" + JSON.stringify(result) + "\n-----" )
		});
	});
}	

function testCredits () {
	var p1 = qx.init(config);
	p1.then( result => {
		var p2 = qx.getMyCredits();
		p2.then ( result => { console.log ("---- MY CREDITS----\n" + JSON.stringify(result) + "\n----"); } );
	});
}
/*
function testJobs () {
	var p1 = qx.init(config);
	p1.then( result => {
		var filter 	= '{"limit":2}';
		var p2 		= qx.getJobs(filter);
		p2.then ( result => { console.log ("---- JOBS----\n" + JSON.stringify(result) + "\n----"); } );
	});
} */

async function testJobs () {
	await qx.init(config);
	var filter 	= '{"limit":2}';
	var jobs = await qx.getJobs(filter);
	console.log ("---- JOBS----\n" + JSON.stringify(jobs) + "\n----");
}


// Usage: node service-engine.js -p 80801 -f metadata/services.json
try {
	testBackends();
	testCalibration ();
	testExperiment ();
	testCredits();
	testJobs();
}
catch (e){
	console.error(e);
}

