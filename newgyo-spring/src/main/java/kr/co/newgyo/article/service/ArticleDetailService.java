//package kr.co.newgyo.article.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import kr.co.newgyo.article.entity.Article;
//import kr.co.newgyo.article.repository.ArticleRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.Random;
//
//// 이 서비스 클레스는 ArticleService와 합쳐질 가능성 있음
//@Service
//public class ArticleDetailService {
//    // articleRepository 받고
//    ArticleRepository articleRepository;
//
//    public ArticleDetailService(ArticleRepository articleRepository) {
//        this.articleRepository = articleRepository;
//    }
//
//    // 주요 뉴스
//    public Article mainArticle(){
//        // 주요 뉴스인걸 어케 알지
//        // 우선 랜덤 수 생성해서 보여주기
//        Random random = new Random();
//        Long number = random.nextLong(1) + 1;
//
//        // 난수로 기사 가져오기
//        return articleRepository.findById(number)
//                .orElseThrow(() -> new EntityNotFoundException("기사 ID " + number + "를 찾을 수 없습니다."));
//    }
//
//    // 카테고리별 기사 정하는 메서드
//    public String categoryArticle(){
//        // 디폴트 값 -> 전체/인기순
//
//        return "success";
//    }
//
//    // 홈페이지
//    // 메인기사 & 카테고리별 기사 한번에 주기
//    // response 따로 만들기
//    public Article homeArticleData() {
//        // 메인기사 데이터
////        우선 메인 데이터만
//        return mainArticle();
//    }
//
//    // 페이지 이동시 Controller에서 id 값을 받아 뉴스 데이터를 받아 반환한다
//    // thyeamleaf 를 쓰니 model로 넘겨주는것과 json으로 넘겨주는거 중 하나 선택해서 보내기
//    // 디테일 페이지에 필요한 데이터 ->
//    public Article getDetailArticleData(Long id){
//        // 뉴스 데이터 다 보내는데
//        // 필요한 데이터만 보내야하나
//        // 요약 키워드를 가지고 요약내용 바꿔야함
//        return articleRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("기사 ID " + id + "를 찾을 수 없습니다."));
//    }
//}
