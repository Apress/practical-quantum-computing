"use strict";

/**
 * 	Request stuff - http://stackabuse.com/the-node-js-request-module/
 */
const log 		= require('./log');
const request 	= require('request');

/*
 var config = { APItoken: 'dcd6b501ffa4b88a796bc5e526b6cf6d96479aa5616b7126938e1aca27cf6ebb3824ad07b7f3cfb4ea22b03a5b40a3fbc45c7d7c819610172ac21e2f2600213d'
	, 'url': 'https://quantumexperience.ng.bluemix.net/api'
    , 'hub': 'MY_HUB'
    , 'group': 'MY_GROUP'
    , 'project': 'MY_PROJECT'
};
*/
var _config;
var _accessToken;
var _userId;

const _userAgent = 'qiskit-api-py';
const _defaultHdrs = {
	'x-qx-client-application': _userAgent
};

/**
 * POST https://quantumexperience.ng.bluemix.net/api/users/loginWithToken
 * Body: {'apiToken': config.APItoken}
 */
function loginWithToken () {
	let options = {  
		url: _config.url + '/users/loginWithToken',
		form: {'apiToken': _config.APItoken}
	};	
	
	//log.debug("auth=" + JSON.stringify(options));
	return new Promise(function(resolve, reject) {
		// Do async job
		// {"id":"Access tok","ttl":1209600,"created":"2018-04-17T23:30:21.089Z","userId":"userid"}
		request.post(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				var json 		= JSON.parse(body);
				_accessToken	= json.id;
				_userId			= json.userId;
				log.debug("Got User:" + _userId + " Tok:" +  _accessToken);
				resolve(JSON.parse(body));
			}
		});
	})
}

/**
 * GET https://quantumexperience.ng.bluemix.net/api/Backends?access_token=TOK 
 * Headers {'x-qx-client-application': 'qiskit-api-py'}
 */
function backends () {
	let options = {  
		url: _config.url + '/Backends?access_token=' + _accessToken,
		headers: _defaultHdrs
	};	
	log.debug("Backends: " + options.url);
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.get(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}

/**
 * GET https://quantumexperience.ng.bluemix.net/api/Backends/NAME/calibration?access_token=TOK 
 * NAME = ibmqx4,...
 * Headers {'x-qx-client-application': 'qiskit-api-py'}
 */
function calibration (name) {
	let options = {  
		url: _config.url + '/Backends/' + name +'/calibration?access_token=' + _accessToken,
		headers: _defaultHdrs
	};	
	log.debug("Calibration: " + options.url);
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.get(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}

/**
 * GET https://quantumexperience.ng.bluemix.net/api/Backends/NAME/parameters?access_token=TOK 
 * NAME = ibmqx4,...
 * Headers {'x-qx-client-application': 'qiskit-api-py'}
 */
function parameters (name) {
	let options = {  
		url: _config.url + '/Backends/' + name +'/parameters?access_token=' + _accessToken,
		headers: _defaultHdrs
	};	
	log.debug("Parameters: " + options.url);
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.get(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}

/**
 * POST https://quantumexperience.ng.bluemix.net/api/users/loginWithToken
 * Body: "name": "Experiment #20180410193115", "codeType": "QASM2", "qasm": "\n\ninclude \"qelib1.inc\";\nqreg q[5];\ncreg c[5];\nu2(-4*pi/3,2*pi) q[0];\nu2(-3*pi/2,2*pi) q[0];\nu3(-pi,0,-pi) q[0];\nu3(-pi,0,-pi/2) q[0];\nu2(pi,-pi/2) q[0];\nu3(-pi,0,-pi/2) q[0];\nmeasure q -> c;\n"}
 * @param name Experiment name
 * @param qas Quantum assembly code (one line separated by \n)
 * @param shots
 */
function experiment (name, qasm, shots, device) {
	let options = {  
		url: _config.url + '/codes/execute?access_token=' + _accessToken + '&shots=' + shots + '&deviceRunType=' + device,
		headers: {'Content-Type': 'application/json', 'x-qx-client-application': _userAgent} ,
		form: {'name': name, "codeType": "QASM2", "qasm": qasm}
	};	
	log.debug("Run experiment " + name + " POST: " + options.url + " Payload:" + JSON.stringify(options.form) );
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.post(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}

/**
 * Jobs GET https://quantumexperience.ng.bluemix.net/api/Jobs?access_token=TOK&filter={"limit":2}
 * @param filter: {"limit":2}
 */
function jobs (filter) {
	let options = {  
		url: _config.url + '/Jobs?access_token=' + _accessToken + ( filter ? '&filter=' + filter : ''),
		headers: _defaultHdrs
	};	
	log.debug("Jobs: " + options.url);
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.get(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}
/**
 * Get my credits - GET https://quantumexperience.ng.bluemix.net/api/users/ID?access_token=TOK 
 * Headers {'x-qx-client-application': 'qiskit-api-py'}
 */
function credits () {
	let options = {  
		url: _config.url + '/users/' + _userId + '?access_token=' + _accessToken,
		headers: _defaultHdrs
	};	
	log.debug("My Credits: " + options.url);
	
	return new Promise(function(resolve, reject) {
		// Do async job
		request.get(options, function(err, res, body) {
			if (err) {
				reject(err);
			}
			else {
				resolve(JSON.parse(body));
			}
		});
	})
}

module.exports =  {
	init: function (cfg) {
		_config = cfg;
		var debug = _config.debug ? _config.debug : false;
		log.init (debug);
		return loginWithToken ();
	},
	
	getCalibration	: calibration,
	getBackends		: backends,
	getParameters	: parameters,
	runExperiment	: experiment,
	getJobs			: jobs,
	getMyCredits	: credits
}

