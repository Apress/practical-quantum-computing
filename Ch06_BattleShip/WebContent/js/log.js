/**
 * Client logging using the javascript console
 * Requires FF w/ Firebug
 * @param text
 */
function LOGD(text) {
	if ( (typeof(console) != "undefined") && console) {
		console.log("[DBG] " + text);
	}
	if (typeof(log) != "undefined") {
		log.debug(text);
	}
}

function LOGE(text) {
	if ( (typeof(console) != "undefined") && console) {
		console.error("[ERR] " + text);
	}
	if (typeof(log) != "undefined") {
		log.error(text);
	}
}

function LOGI(text) {
	if ((typeof(console) != "undefined") &&  console) {
		console.info("[INF] " + text);
	}
	if (typeof(log) != "undefined") {
		log.info(text);
	}
}

function LOGV(text) {
	if ( (typeof(console) != "undefined") && console) {
		console.log("[LOG] " + text);
	}
}

function LOGW(text) {
	if ( (typeof(console) != "undefined") &&  console) {
		console.warn("[WRN] " + text);
	}
	if (typeof(log) != "undefined") {
		log.warn(text);
	}
}

/**
 * Log grouping
 * @param text
 */
function LOG_GRP_S(text) {
	if ( console && console.group) {
		console.group(text);
	}
	else {
		console.log("[DBG] " + text);
	}
	if (typeof(log) != "undefined") {
		log.debug(text);
	}
}
function LOG_GRP_E() {
	if ( console && console.groupEnd) {
		console.groupEnd();
	}
}
		

