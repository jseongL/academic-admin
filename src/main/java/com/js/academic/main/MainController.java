package com.js.academic.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.js.academic.crawler.NoticeCrawler; // 1. 크롤러 임포트
import com.js.academic.user.domain.User;
import com.js.academic.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private final UserService userService;
    private final NoticeCrawler noticeCrawler; // 2. 크롤러 필드 선언

    // 3. 생성자 주입 방식으로 UserService와 NoticeCrawler를 함께 주입받음
    public MainController(UserService userService, NoticeCrawler noticeCrawler) {
        this.userService = userService;
        this.noticeCrawler = noticeCrawler;
    }

    // 메인 페이지 (로그인 전 화면 등)
    @GetMapping("/main-view")
    public String mainView(HttpSession session, Model model) {
        return "main/main"; // templates/main/main.html 연결
    }
    
    // 대시보드 페이지 (로그인 후 메인 포털 화면)
    @GetMapping("/dashboard")
    public String dashboardView(HttpSession session, Model model) {
        // 1. 세션에서 로그인한 유저 정보 꺼내기
        User loginUser = (User) session.getAttribute("loginUser");
        
       
        
        // 2. 로그인하지 않은 상태로 접근하면 메인 페이지로 강제 이동 (보안)
        if (loginUser == null) {
            return "redirect:/main-view"; 
        }
        
        // 3. 실제 가천대 공지사항 크롤링 데이터 가져오기
        List<Map<String, String>> noticeList = noticeCrawler.getNoticeList();
        
        // 4. 뷰(HTML)에서 쓸 수 있도록 Model에 담아서 전달
        model.addAttribute("user", loginUser);
        model.addAttribute("noticeList", noticeList); // 공지사항 목록 추가
        
        return "main/dashboard";
    }
    
    
}