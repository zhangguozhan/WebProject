var ws = null;
$(document).ready(function(){
	if('WebSocket' in window) {
		setMessageInnerHEML('浏览器支持WebSocket，开始创建websocket对象！');
		ws = new WebSocket("ws://127.0.0.1:8080/WebProject/websocket.do");
		ws.onerror = function() {
			setMessageInnerHEML('websocket connection error !');
		};
		
		ws.onopen = function() {
			setMessageInnerHEML('websocket connection success !');
		};
		
		ws.onmessage = function(event) {
			setMessageInnerHEML(event.data);
		};
		
		ws.onclose = function() {
			setMessageInnerHEML('websocket colsed !');
		};
		
		window.onbeforeunload = function() {
			closeWebSocket();
		};
	}else {
		alert('当前浏览器不支持WebSocket');
	}
});

function setMessageInnerHEML(innerHTML) {
	document.getElementById('message').innerHTML += innerHTML+'<br/>';
}

function send() {
	var message = document.getElementById('text').value;
	ws.send(message);
}

function closeWebSocket() {
	ws.close();
}