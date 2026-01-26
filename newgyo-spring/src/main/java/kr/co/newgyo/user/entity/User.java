package kr.co.newgyo.user.entity;

import jakarta.persistence.*;
import kr.co.newgyo.user.enums.LoginProvider;
import kr.co.newgyo.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true) // toBuilder = true -> 이미 만들어진 객체를 기반으로 새로운 빌더를 만들어서 일부만 수정할 수 있게 해줌
@NoArgsConstructor // 매개변수가 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자
@Table(name="USER")
public class User {

    // 유저 엔티티는 필드 주입으로 바꿔야 하나

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // null 가능 -> 카카오톡 로그인 없는 사람
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOKEN_ID")
    private Token token;

    @Column(nullable = false)
    private String username; // 카카오 사용자인지 확인 필요 반대로 카카오는 기존 사용자인지

    @Builder.Default
    @Column(nullable = false, columnDefinition = "varchar(100) default 'KAKAO'")
    private String password = "KAKAO"; // 아이디 암호화 해서 저장 or 카카오 로그인 사용자는 null
    // @Builder.Default를 사용할 때는 초기값 주기 @Column 으로 지정해 줬지만 필드에도 줘야한다.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 권한 이넘으로 주기

    //@Builder.Default를 붙이면 빌더로 객체 생성할 때
    //명시적으로 값을 넣지 않아도 자동으로 Default값이 들어갑니다.
    //columnDefinition = JPA가 테이블을 자동 생성할 때(DDL 생성 시) 사용하는 실제 데이터베이스 컬럼 정의 문장을 직접 지정
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isSubscribed = false; // 구독 버튼 따로

    // 사용자가 어떤 로그인 방식으로 가입했는지 LOCAL,KAKAO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginProvider provider;

    public void assignToken(Token token){
        this.token = token;
    }

    // 구독 데이터 변경
    // 편의 메소드 JavaBeans 규약 차이
    public void chSubscribe(Boolean chSub){
        this.isSubscribed = chSub;
    }
}
