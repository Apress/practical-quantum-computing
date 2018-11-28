/**
 * 
 */

// Server WS endpoint
var END_POINT = "ws://localhost:8080/BattleShip/WSBattleship";

// Random ID used to track a client
var CLIENT_ID = Math.floor(Math.random() * 10000);  

function WS_connect(host) {
    LOGD("WS Connect " + host);

    if ('WebSocket' in window) {
        this.socket = new WebSocket(host);
    } else if ('MozWebSocket' in window) {
        this.socket = new MozWebSocket(host);
    } else {
        LOGE('Error: WebSocket is not supported by this browser.');
        return;
    }

    this.socket.onopen = function() {
        LOGD('WS Opened ' + host); 
    };

    this.socket.onclose = function() {
        LOGD('WS Closed ' + host);
    };

    this.socket.onmessage = function(message) {
        // { status: 200 , message :'...'}
        LOGD('OnMessage: ' + message.data);
        var json = JSON.parse(message.data);
        
        if ( json.status >= 300 && json.status < 400) {
            // warning 
            notify(json.message, 'warning');
        }
        if ( json.status >= 400 ) {
            // error
            notify(json.message, 'danger');
            return;
        }
        handleResponse (json);
    };
}

function WS_initialize () { 
    var clientId = CLIENT_ID;
    var host     = END_POINT;
    this.url     = host + '?clientId=' + clientId; 
    
    WS_connect(this.url);
};

function WS_send (text) {
    this.socket.send(text);
};
