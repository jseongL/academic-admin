package com.js.academic.notice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.js.academic.crawler.NoticeCrawler;
import com.js.academic.user.domain.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class NoticeController {

    private final NoticeCrawler noticeCrawler;

    public NoticeController(NoticeCrawler noticeCrawler) {
        this.noticeCrawler = noticeCrawler;
    }

    // 공지사항 상세 페이지 매핑 (/notice/detail?id=글번호&title=제목&date=날짜)
    @GetMapping("/notice/detail")
    public String noticeDetail(
            @RequestParam("id") String articleId,
            @RequestParam(value = "title", defaultValue = "제목 없음") String title,
            @RequestParam(value = "date", defaultValue = "") String date,
            HttpSession session, 
            Model model) {
        
        // 1. 로그인 보안 체크
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/main-view";
        }

        // 2. 전달받은 데이터를 Map에 담기
        Map<String, String> notice = new HashMap<>();
        notice.put("title", title);
        notice.put("date", date);
        notice.put("content", "가천대학교 보안 정책 및 암호화된 링크 구조로 인해 본문은 학교 원문 페이지에서 확인하실 수 있습니다.");

        // 3. 모델에 데이터 담기
        model.addAttribute("user", loginUser);
        model.addAttribute("articleId", articleId);
        model.addAttribute("notice", notice);

        return "notice/detail"; 
    }
}