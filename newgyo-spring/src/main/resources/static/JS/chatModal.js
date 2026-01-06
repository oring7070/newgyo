const button = document.getElementById('chatbot-button');
const modal = document.getElementById('chatbot-modal');
const closeBtn = document.querySelector('.close-btn');
const chatArea = document.getElementById('chatArea');

// 버튼 클릭 → 채팅창 열기/닫기
button.addEventListener('click', () => {
    modal.classList.toggle('open');
    // 자동 스크롤 아래로
    chatArea.scrollTop = chatArea.scrollHeight;
});

// 닫기 버튼
closeBtn.addEventListener('click', () => {
    modal.classList.remove('open');
});

// 모달 바깥 클릭 시 닫기 (선택사항)
window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.classList.remove('open');
    }
});