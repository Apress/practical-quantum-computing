/**
 * Bootstrap Growl notifications 
 * 
 * See http://bootstrap-notify.remabledesigns.com/
 * 
 * Requires: JQuery
 */

/**
 * Display a notification on the top right of the window:
 * <pre>
 * notify('Welcome back.', 'warning');
 * </pre>
 * 
 * @param message The text
 * @param type Notification type: success, info, danger , inverse, warning.
 */
function notify(message, type) {
	$.growl({
		message : message
	/*
	 * icon: 'glyphicon glyphicon-warning-sign', url:
	 * 'https://github.com/mouse0270/bootstrap-notify', target: '_blank'
	 */
	}, {
		type : type,
		allow_dismiss : true,
		label : 'Cancel',
		className : 'btn-xs btn-inverse',
		placement : {
			from : 'top',
			align : 'right'
		},
		delay : 10000,
		animate : {
			enter : 'animated fadeIn',
			exit : 'animated fadeOut'
		},
		offset : {
			x : 20,
			y : 85
		}
	});
};
