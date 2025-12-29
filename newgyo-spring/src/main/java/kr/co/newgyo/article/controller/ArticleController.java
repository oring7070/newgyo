package kr.co.newgyo.article.controller;

//import kr.co.newgyo.article.entity.Article;
//import kr.co.newgyo.article.service.ArticleDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class ArticleController {
//    // ArticleService 받기
//    ArticleDetailService articleDetailService;
//
//    public ArticleController(ArticleDetailService articleDetailService) {
//        this.articleDetailService = articleDetailService;
//    }

    @GetMapping("/domestic")
    public String domestic(Model model){
        model.addAttribute("domestic",null);
        return "article";
    }

    @GetMapping("/international")
    public String international(Model model){
        model.addAttribute("international", null);
        return "article";
    }

    //상세페이지 디테일 페이지
    // 주소에 id 값을 같이 받아오게
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model){
        // 디비에 해당하는 id값의 뉴스가 없을 때

        // 데이터는 json으로?
//        model.addAttribute("article",articleDetailService.getDetailArticleData(id));

        return "articleDetail";
    }
}