// .main-news, .small-news-item 요소를 클릭했을 때 상세페이지로 이동
document.addEventListener('click', function(e) {
    const target = e.target.closest('.main-news, .small-news-item');
    if (!target) return;

    const id = target.dataset.id;
    if (id) {
        location.href = `/article/${id}`
    }
})