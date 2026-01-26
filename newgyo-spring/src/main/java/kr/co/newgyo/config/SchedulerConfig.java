package kr.co.newgyo.config;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Keyword;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.subscribe.repository.SubscribeRepository;
import kr.co.newgyo.user.entity.User;
import kr.co.newgyo.user.entity.UserKeyword;
import kr.co.newgyo.user.repository.UserRepository;
import kr.co.newgyo.user.service.KakaoService;
import kr.co.newgyo.user.service.TokenCryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    // 왜 여긴 생성자 없이 주입 되냐? Config라 그런가 주입. 빈 재대로 이해하기
    private final KakaoService service;
    private final UserRepository userRepository;
    private final TokenCryptoService tokenCryptoService;
    private final ArticleRepository articleRepository;
    private final SubscribeRepository subscribeRepository;

    // 매일 아침 8시 0분 0초에 실행
    @Transactional
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendDailyMorningMessage() {

        // 구독한 유저 데이터 가져오기
        List<User> userList = userRepository.findByIsSubscribed(true);

        // n+1 지옥
        // in절 사용 (카테고리 데이터)
        // 핵심은 "반복문 안에서 쿼리를 날리지 말고, 필요한 ID들을 먼저 다 모아서 한 번에 조회하는 것

        // 유저 ID들만 따로 모으기
        List<Long> userIds = new ArrayList<>();
        for(User user : userList){
            userIds.add(user.getId());
        }

        // 해당 유저들의 키워드 전체 조회
        List<UserKeyword> allUserKeywords = subscribeRepository.findByUserIdIn(userIds);

        // 유저별로 키워드를 담아둘 바구니 map 만들기
        Map<Long, List<UserKeyword>> userKeywordMap = new HashMap<>();

        for(UserKeyword uk : allUserKeywords){
            Long userId = uk.getUser().getId();
            // 1. 유저 ID가 Map에 아직 없다면 (이 유저를 처음 만났다면)
            if (!userKeywordMap.containsKey(userId)) {
                // 2. 이 유저 전용의 새로운 리스트(바구니)를 생성해서 Map에 넣어준다.
                userKeywordMap.put(userId, new ArrayList<>());
            }

            // 3. Map에서 이 유저의 리스트를 꺼내와서, 현재 키워드(uk)를 추가한다.
            userKeywordMap.get(userId).add(uk);
        }

        // 중복 없이 기사 검색에 필요한 키워드 추출
        Set<Long> keywords = new HashSet<>();

        for(List<UserKeyword> value  : userKeywordMap.values()){
            for( UserKeyword  keyValue : value){

                if( keyValue.getKeyword() != null) {
                    keywords.add(keyValue.getKeyword().getId());
                }
            }
        }

        // 키워드별 기사 map
        List<Article> articles = articleRepository.findByKeywordIdIn(keywords);

        // 1. 조회된 기사들을 키워드 ID별로 먼저 분류 (Map<KeywordId, List<Article>>)
        Map<Long, List<Article>> keywordToArticlesMap = new HashMap<>();
        for (Article ar : articles) {
            Long kId = ar.getKeyword().getId(); // Article이 가진 키워드 ID 추출

            if (!keywordToArticlesMap.containsKey(kId)) {
                keywordToArticlesMap.put(kId, new ArrayList<>());
            }
            keywordToArticlesMap.get(kId).add(ar);
        }

        // 2. 최종 결과물 담을 Map 생성
        Map<Long, List<Article>> totalArticleList = new HashMap<>();

        // 3. 유저별로 루프를 돌며 기사 매칭
        for (Map.Entry<Long, List<UserKeyword>> entry : userKeywordMap.entrySet()) {
            Long userId = entry.getKey();
            List<UserKeyword> userKeywords = entry.getValue();

            // 이 유저에게 배정될 기사들 (중복 방지를 위해 Set 사용 권장)
            Set<Article> userRecommendedSet = new HashSet<>();

            for (UserKeyword uk : userKeywords) {
                Long userKId = uk.getKeyword().getId();

                // 해당 키워드에 속한 기사 뭉치를 가져와서 추가
                List<Article> matchedArticles = keywordToArticlesMap.get(userKId);
                if (matchedArticles != null) {
                    userRecommendedSet.addAll(matchedArticles);
                }
            }

            // Set을 List로 변환하여 저장
            totalArticleList.put(userId, new ArrayList<>(userRecommendedSet));
        }

        // 유저별 토큰으로 기사 보내기
        for (Map.Entry<Long, List<Article>> entry : totalArticleList.entrySet()) {
            // n+1 문제 해결해야함
            // 해당 유저의 id로 토큰을 불러와 복호화 후 저장
            // id 말고 토큰으로 바로 저장하도록 로직 변경 하기
            String userToken = tokenCryptoService.decryptRefreshToken(userRepository.findById(entry.getKey()).getToken().getRefreshToken());
            // 현재는 기사의 첫번째 하나 보내주지만 어떤걸 보내줄지 정하기 랜덤?
            Article article = entry.getValue().get(1);
            try {
                service.sendTextToMe(userToken, article.getContent());

                Thread.sleep(1500);
            } catch (Exception e) {
               log.warn("메시지 전송 실패 - userId: {}, 이유: {}", userToken, e.getMessage());
            }

        }

        // 고도화 -> 현재 스케쥴러 for 문을 딜레이를 통해 보내주고 있지만 레디스, 배치 등 사용해서 바꾸기
        // 보내지 못한 사람 알아서 왜인지, 다시 보내기 등

    }

    // 컬렉션 관련 공부 해야하고
    // 패치조인 처리 필요한부분은 패치조인으로 처리하기
}
