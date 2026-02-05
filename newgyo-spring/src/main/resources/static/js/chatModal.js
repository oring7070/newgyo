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

// 구독 버튼 클릭 시 모달 열기
document.getElementById('subscription-button').addEventListener('click', function() {
    document.getElementById('subscription-modal').classList.add('open');
});

// 닫기 버튼
document.querySelector('.close-sub-btn').addEventListener('click', function() {
    document.getElementById('subscription-modal').classList.remove('open');
});

// 모달 바깥 클릭 시 닫기 (선택사항)
document.getElementById('subscription-modal').addEventListener('click', function(e) {
    if (e.target === this) {
        this.classList.remove('open');
    }
});

// 구독 버튼 클릭 시 실행
document.querySelector('.subscribe-btn').addEventListener('click', () => {
    const checkedCategories = Array.from(
        document.querySelectorAll('.plan-benefits input[name="category"]:checked')
    ).map(checkbox => checkbox.value);
    const token = localStorage.getItem('Authorization');

    // 로그인 중인지 확인 (변경 사항)


    if (checkedCategories.length === 0) {
        alert('최소 1개 이상의 분야를 선택해주세요.');
        return;
    }

    const planData = {
        content: "subscribe",
        includedCategories: checkedCategories
    };

    // 실제 서버로 전송 예시
    fetch('/api/subscribe', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token
        },
        body: JSON.stringify(planData)
    })
    .then(response => response.json())
    .then(data => {
        alert('구독이 완료되었습니다!');
    })
    .catch(error => {
        console.error('구독 오류:', error);
        alert('구독 처리 중 오류가 발생했습니다.');
    });

});

// JWT 토큰에서 payload 부분만 디코딩 (서명 검증 없이 읽기만 함)
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Invalid JWT format", e);
        return null;
    }
}