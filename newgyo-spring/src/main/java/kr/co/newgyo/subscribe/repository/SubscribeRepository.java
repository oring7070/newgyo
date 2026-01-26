package kr.co.newgyo.subscribe.repository;


import kr.co.newgyo.user.entity.User;
import kr.co.newgyo.user.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<UserKeyword,Integer> {

    // 특정 유저의 카테고리들
    List<UserKeyword> findByUserId(Long userid);
    List<UserKeyword> findByUserIdIn(List<Long> userid);
}
