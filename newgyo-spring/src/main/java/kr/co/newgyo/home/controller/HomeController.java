package kr.co.newgyo.home.controller;

import kr.co.newgyo.article.service.ArticleService;
import kr.co.newgyo.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService service;

    @GetMapping
    public String home(@RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false, defaultValue = "popular") String sort,
                       Model model){
        model.addAttribute("article", service.home(categoryId, sort));
        model.addAttribute("category", service.getAllCategory());

        // 필터
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        return "home";
    }
}
