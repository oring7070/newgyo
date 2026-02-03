package kr.co.newgyo.home.service;

import kr.co.newgyo.article.entity.Keyword;
import kr.co.newgyo.article.service.ArticleService;
import kr.co.newgyo.home.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final ArticleService articleService;

    public HomeResponse home(Long categoryId, String sort){
        // 로직 가져올지
        return articleService.home(categoryId, sort);
    }

    public List<Keyword> getAllCategory(){
        return articleService.getAllCategory();
    }
}
