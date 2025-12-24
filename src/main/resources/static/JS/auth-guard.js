async function applyJwtAuth() {
    // 이미 적용된 페이지면 중복 실행 방지
    if (window.jwtAuthApplied) {
        return;
    }
    window.jwtAuthApplied = true;

    const token = localStorage.getItem('Authorization');
    const publicPaths = ['/login-page', '/join-page', '/login', '/join'];

    console.log('[JWT Guard] 현재 경로:', window.location.pathname);
    console.log('[JWT Guard] 토큰 존재:', !!token);

    if (publicPaths.includes(window.location.pathname)) {
        console.log('[JWT Guard] 공개 페이지 → 스킵');
        return;
    }

    if (!token) {
        console.log('[JWT Guard] 토큰 없음 → 로그인 페이지로 이동');
        window.location.href = '/login-page';
        return;
    }

    try {
        const response = await fetch(window.location.pathname + window.location.search, {
            method: 'GET',
            headers: {
                'Authorization': token
            }
        });

        console.log('[JWT Guard] 서버 응답 상태:', response.status);

        if (response.ok) {
            const html = await response.text();
            document.open();
            document.write(html);
            document.close();
            console.log('[JWT Guard] 페이지 성공적으로 재로드');
        } else {
            alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
            localStorage.removeItem('Authorization');
            window.location.href = '/login-page';
        }
    } catch (err) {
        console.error('[JWT Guard] 네트워크 오류:', err);
        window.location.href = '/login-page';
    }
}

// 페이지 로드 시 실행
window.addEventListener('load', applyJwtAuth);