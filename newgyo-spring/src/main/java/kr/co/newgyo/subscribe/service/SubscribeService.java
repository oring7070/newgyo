package kr.co.newgyo.subscribe.service;

import kr.co.newgyo.article.entity.Keyword;
import kr.co.newgyo.article.repository.KeywordRepository;
import kr.co.newgyo.security.CustomUserDetails;
import kr.co.newgyo.subscribe.dto.KeywordSubscribeRequest;
import kr.co.newgyo.subscribe.repository.SubscribeRepository;
import kr.co.newgyo.user.entity.User;
import kr.co.newgyo.user.entity.UserKeyword;
import kr.co.newgyo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscribeService {
        private final UserRepository userRepository;
        private final KeywordRepository keywordRepository;
        private final SubscribeRepository subscribeRepository;

    public SubscribeService(UserRepository userRepository, KeywordRepository keywordRepository, SubscribeRepository subscribeRepository) {
        this.userRepository = userRepository;
        this.keywordRepository = keywordRepository;
        this.subscribeRepository = subscribeRepository;
    }

    // 사용자 구독 데이터 변경
    public void firstSubscribe(CustomUserDetails userDetails){
        // 유저가 구독이 되어있는지 확인 false 면 구독않한 상황 -> exists로 할 수 있나 확인
        User user = userRepository.findByUsername(userDetails.getUsername());

        // if는 안이 트루일때 동작 -> 엔티티 안에서 해결하기 (예정)
        // 엔티티 내부에 로직 담기 (도메인 모델 패턴)
        if(!user.getIsSubscribed()){
            // 구독이 안되어 있는 상황 - 사용자 구독 데이터 변경
            user.chSubscribe(true);
        }

    }

    // 클라이언트에서 받은 데이터 저장하기
    // 트렌젝션 어노테이션
    public void savaUserKeyword(CustomUserDetails userDetails, KeywordSubscribeRequest keywordList){

        // 유저 객체 생성
        User userOb = userRepository.findByUsername(userDetails.getUsername());

        // 유저키워드 객체 생성
        UserKeyword userKeywordOb;

        // 리스트 데이터 사용해서 유저키워드 테이블 데이터 저장
        // 포문 사용해서 리스트 값 마다 저장한다
        List<String> categories = keywordList.includedCategories();

        // 레코드 객체 생성
        // 각 레코드와 유저 데이터를 유저키워드에 저장하는 로직 구현 -> 최적화
        // for 문에서 jpa crud를 사용할 땐 쿼리 한번으로 끝낼 수 있나 생각하자
        List<Keyword> keywordOb = keywordRepository.findByKeywordIn(categories);

        for( Keyword keyword : keywordOb){

            // 생성된 유저와 레코드 객체로 유저키워드 저장
             userKeywordOb = UserKeyword.builder()
                     .user(userOb)
                     .keyword(keyword)
                     .build();

            subscribeRepository.save(userKeywordOb); // saveAll 생각해보기

        }
    }
}
