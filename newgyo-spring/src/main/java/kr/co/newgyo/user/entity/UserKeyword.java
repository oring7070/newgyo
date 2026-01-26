package kr.co.newgyo.user.entity;

import jakarta.persistence.*;
import kr.co.newgyo.article.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name="USER_KEYWORD")
public class UserKeyword {

    @Id
    @Column(name="USER_KETWORD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 중복 방지를 위해
    // @UniqueConstraint 또는 uniqueConstraints를 추가

    //user와 매핑 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    //keyword와 매핑 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="KEYWORD_ID")
    private Keyword keyword;

    //단방향으로 연결테이블로 데이터 조회할텐데 되나?
    // 예시 - User가 구독한 카테고리 목록 조회
//    @Query("SELECT uc.category FROM UserCategory uc WHERE uc.user.id = :userId AND uc.active = true")
//    List<Category> findSubscribedCategoriesByUserId(@Param("userId") Long userId);
//
//    // 또는 엔티티 그래프 활용
//    List<UserCategory> findByUserIdAndActiveTrue(Long userId);
}
