const socket = new SockJS('/ws-chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function() {
    // 모든 사용자가 아닌 사용자 한명에게만 보내도록 구독
    // 정보는 socket연결시 클라이언트 헤더에 저장된 토큰을 이용
    // stompClient.subscribe 은 구독 및 서버에서 오는 데이터 수신
    stompClient.subscribe('/user/queue/reply', function(message) {
        const chat = JSON.parse(message.body);
        addMessage(chat.sender || 'AI Bot', chat.content);
    });
    loadChatHistory();  // 연결 후 기록 로드
});

// 대화 기록 배열 (메모리)
let chatHistory = [];

// 채팅 기록 로드 (sessionStorage에서)
function loadChatHistory() {
    const storedHistory = sessionStorage.getItem('chatHistory');
    if (storedHistory) {
        chatHistory = JSON.parse(storedHistory);
        chatHistory.forEach(msg => displayMessage(msg.sender, msg.content));
    } else {
        // 초기 메시지 (처음 열릴 때만)
        addMessage('AI Bot', '안녕하세요! 어떻게 도와드릴까요?');
    }
}

// 메시지 추가 & 저장
function addMessage(sender, content) {
    chatHistory.push({ sender, content });
    // 먼저 내 메시지를 화면에 바로 표시
    displayMessage(sender, content);
    saveChatHistory();  // sessionStorage에 저장
}

// 채팅 기록 저장 (sessionStorage에)
function saveChatHistory() {
    sessionStorage.setItem('chatHistory', JSON.stringify(chatHistory));
}

function send() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    if (content) {
        addMessage('User', content); // 내 메시지 추가 & 저장

        const chatMessage = {
            sender: 'User',  // 실제로는 로그인 사용자 이름 (헤더에서 가져오기)
            content: content
        };

        // /app/chat.send 로 전송
        // 서버에 메세지 전송만
        stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
        input.value = '';
    }
}

function displayMessage(sender, content) {
    const chatArea = document.getElementById('chatArea');

    // 새로운 메시지 요소 생성
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message');

    // sender에 따라 user 또는 bot 클래스 추가
    if (sender === 'User') {
        messageDiv.classList.add('user');
    } else {
        messageDiv.classList.add('bot');
    }

    // HTML 구조
    messageDiv.innerHTML = `
        <div class="sender-name">${sender}</div>
        <div class="bubble">${content}</div>
    `;

    // 채팅 영역에 추가
    chatArea.appendChild(messageDiv);

    // 자동 스크롤 아래로
    chatArea.scrollTop = chatArea.scrollHeight;
}
