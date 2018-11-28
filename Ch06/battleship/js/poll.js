/**
 * @param cfg Poll config: Object with keys: 
 * <pre> cfg.done 	= poll_done;	// DONE callback
 * cfg.fail 	= poll_fail;	// FAIL callback
 * cfg.timeoutDone = 5000;		// DONE timeout (ms)
 * cfg.timeoutFail = 15000;		// FAIL timeout (ms)
 * cfg.checkStatus = true;		// If true check the JSON response HTTP (status) key.
 * cfg.debug 	= false;		// If true debug msgs
 * </pre>
 */
function Poller( cfg ) {
	
	return { 
		start : function () {
			__poll(cfg)
		}
	}
}

function __poll(cfg) {
	var url 		= cfg.url 			|| null;
	var toOK		= cfg.timeoutDone 	|| 100;
	var toFail		= cfg.timeoutFail 	|| 10000;
	var abortOnFail	= typeof(cfg.abortOnFail) 	!= 'undefined' ? cfg.abortOnFail : true;
	var debug		= typeof(cfg.debug) 		!= 'undefined' ? cfg.debug : false;
	
	if ( !url )		{ LOGE('Poll: Url is required'); return; }
	if ( debug) 	LOGD("[POLL] Url: " + url);
	
	$.ajax({
		type : 'GET',
		url : url,
		// request response in json!
		headers : {
			"Accept" : "application/json; charset=utf-8"
		},
		cache : false
	})
	.done(function (json) {
		if ( debug) LOGD("[POLL] JSON: " + JSON.stringify(json));
		
		if ( cfg.done) {
			cfg.done(json);
		}
		if ( abortOnFail && typeof(json.status) != 'undefined' && (json.status > 300) ) {
			LOGE('[POLL] ' + json.status + ': ' + json.message);
		}
		else {
			// LONG poll recurse
			setTimeout(function() { __poll(cfg); }, toOK);
		}
		
	})
	.fail(function (jqXHR, textStatus) {
		LOGW("Poll failed with status: " + textStatus);

		if ( cfg.fail) {
			cfg.fail(jqXHR, textStatus);
		}
		// recurse in case the long poll expired
		setTimeout(function() { __poll(cfg); }, toFail);
	});
}
