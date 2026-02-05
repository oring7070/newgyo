document.getElementById('signupForm').addEventListener('submit', async function(e) {
    e.preventDefault();  // 기본 form submit 완전 차단

    const form = this;
    const password = document.getElementById('password').value;
    const confirm = document.getElementById('passwordConfirm').value;

    let isValid = true;

    // 1. 비밀번호 일치 체크
    if (password !== confirm) {
        showError('passwordConfirm', true);
        isValid = false;
    } else {
        showError('passwordConfirm', false);
    }

    // 2. HTML5 기본 검증
    if (!form.checkValidity()) {
        isValid = false;
    }

    // 3. 모든 입력 필드 에러 표시
    document.querySelectorAll('input').forEach(input => {
        if (!input.checkValidity()) {
            showError(input.id, true);
            isValid = false;
        } else {
            showError(input.id, false);
        }
    });

    if (!isValid) {
        return;  // 검증 실패 → 제출 안 함
    }

    // 4. 폼 데이터 수집 (JSON으로 변환)
    const formData = {
        username: document.getElementById('email').value,
        password: document.getElementById('password').value,
        // passwordConfirm은 서버에 보낼 필요 없음
        // 닉네임 등 추가 필드가 있으면 여기에 넣으세요
    };

    try {
        const response = await fetch('/join/inputJoin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            // 성공 → 리다이렉트
            window.location.href = '/loginPage';
        } else {
            // 실패 처리 (서버에서 에러 메시지 반환했다고 가정)
            const errorData = await response.json();
            alert(errorData.message || '회원가입에 실패했습니다.');
        }
    } catch (error) {
        console.error('회원가입 오류:', error);
        alert('서버와의 연결에 문제가 있습니다.');
    }
});

// 실시간 입력 검증
document.querySelectorAll('input').forEach(input => {
    input.addEventListener('input', function() {
        if (this.checkValidity()) {
            showError(this.id, false);
        }
    });
});

// 비밀번호 확인 실시간 체크
document.getElementById('passwordConfirm').addEventListener('input', function() {
    const password = document.getElementById('password').value;
    showError('passwordConfirm', this.value !== password);
});

// 에러 표시/숨김 함수
function showError(inputId, show) {
    const input = document.getElementById(inputId);
    const errorDiv = input.parentElement.querySelector('.error-message');

    if (errorDiv) {
        errorDiv.style.display = show ? 'block' : 'none';
        input.classList.toggle('is-invalid', show);
    }
}