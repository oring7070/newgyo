package kr.co.newgyo.user.repository;


import kr.co.newgyo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {

    Boolean existsByUsername(String username);

    User findByUsername(String username);

    User findById(Long tokenId);

    // 구독 데이터로 판단
    List<User> findByIsSubscribed(boolean bool);
}
