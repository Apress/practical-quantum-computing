var _debug = false;

function LOGD( tag, txt ) {
	if ( _debug ) {
		console.log('[DBG-QX] ' + tag + ' '  + (txt ? txt : ''));
	}
}

function LOGE( tag, txt ) {
	console.error('[ERR-QX] ' + tag + ' ' + (txt ? txt : ''));
}

function buildStatus(code, text ) {
	return { 'code' : code, 'message': text};
}

function init (debug) {
	_debug = debug;
}

exports.init = init;
exports.debug = LOGD;
exports.error = LOGE;
exports.status = buildStatus;