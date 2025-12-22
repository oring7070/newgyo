package kr.co.newgyo.article.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class ArticleController {

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
}