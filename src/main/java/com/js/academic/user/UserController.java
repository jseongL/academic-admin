package com.js.academic.user;

import com.js.academic.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 목록 페이지 (http://localhost:8080/user/list-view)
    @GetMapping("/list-view")
    public String userListView(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "user/list"; // templates/user/list.html 연결
    }

    // 회원가입 페이지 (http://localhost:8080/user/signup-view)
    @GetMapping("/signup-view")
    public String signupView() {
        return "user/signup"; // templates/user/signup.html 연결
    }
}