package com.js.academic.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.js.academic.crawler.MealCrawler;
import com.js.academic.crawler.MealInfo;
import com.js.academic.crawler.NoticeCrawler;
import com.js.academic.user.domain.User;
import com.js.academic.user.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    private final UserService userService;
    private final NoticeCrawler noticeCrawler;
    private final MealCrawler mealCrawler;

    public MainController(
            UserService userService,
            NoticeCrawler noticeCrawler,
            MealCrawler mealCrawler) {

        this.userService = userService;
        this.noticeCrawler = noticeCrawler;
        this.mealCrawler = mealCrawler;
    }

    @GetMapping("/main-view")
    public String mainView(
            HttpSession session,
            Model model) {

        return "main/main";
    }

    @GetMapping("/dashboard")
    public String dashboardView(
            @RequestParam(
                    value = "mealDate",
                    required = false
            ) String mealDate,
            HttpSession session,
            Model model) {

        // ==========================================
        // 로그인 확인
        // ==========================================

        User loginUser =
                (User) session.getAttribute(
                        "loginUser"
                );

        if (loginUser == null) {
            return "redirect:/main-view";
        }

        // ==========================================
        // 공지사항
        // ==========================================

        List<Map<String, String>> noticeList =
                noticeCrawler.getNoticeList();

        // ==========================================
        // 선택된 식단 날짜
        //
        // /dashboard
        // → 오늘
        //
        // /dashboard?mealDate=2026-09-18
        // → 2026-09-18
        // ==========================================

        LocalDate selectedDate =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        if (mealDate != null
                && !mealDate.isBlank()) {

            try {

                selectedDate =
                        LocalDate.parse(
                                mealDate
                        );

            } catch (DateTimeParseException e) {

                // 잘못된 날짜가 들어오면 오늘 날짜 사용
                selectedDate =
                        LocalDate.now(
                                ZoneId.of(
                                        "Asia/Seoul"
                                )
                        );
            }
        }

        // ==========================================
        // 현재 선택 날짜가 속한 주 계산
        // 월요일 ~ 일요일
        // ==========================================

        LocalDate weekStart =
                selectedDate.with(
                        TemporalAdjusters
                                .previousOrSame(
                                        DayOfWeek.MONDAY
                                )
                );

        LocalDate weekEnd =
                selectedDate.with(
                        TemporalAdjusters
                                .nextOrSame(
                                        DayOfWeek.SUNDAY
                                )
                );

        // ==========================================
        // 이전 주 / 다음 주
        //
        // 현재 선택한 요일을 그대로 유지하면서
        // 7일 이전 / 이후로 이동
        // ==========================================

        LocalDate previousWeekDate =
                selectedDate.minusWeeks(1);

        LocalDate nextWeekDate =
                selectedDate.plusWeeks(1);

        // ==========================================
        // 표시용 날짜 Formatter
        // ==========================================

        DateTimeFormatter weekFormatter =
                DateTimeFormatter.ofPattern(
                        "yyyy.MM.dd"
                );

        DateTimeFormatter selectedDateFormatter =
                DateTimeFormatter.ofPattern(
                        "yyyy.MM.dd(E)",
                        Locale.KOREAN
                );

        DateTimeFormatter dayFormatter =
                DateTimeFormatter.ofPattern(
                        "E MM.dd",
                        Locale.KOREAN
                );

        // ==========================================
        // 2026.09.14 ~ 2026.09.20
        // ==========================================

        String mealWeekRange =
                weekStart.format(
                        weekFormatter
                )
                + " ~ "
                + weekEnd.format(
                        weekFormatter
                );

        // ==========================================
        // 월~일 날짜 버튼 생성
        // ==========================================

        List<Map<String, Object>> mealWeekDays =
                new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate day =
                    weekStart.plusDays(i);

            Map<String, Object> dayData =
                    new HashMap<>();

            dayData.put(
                    "date",
                    day.toString()
            );

            dayData.put(
                    "label",
                    day.format(
                            dayFormatter
                    )
            );

            dayData.put(
                    "active",
                    day.equals(
                            selectedDate
                    )
            );

            mealWeekDays.add(
                    dayData
            );
        }

        // ==========================================
        // 선택된 날짜의 실제 식단 크롤링
        // ==========================================

        MealInfo visionMeal =
                mealCrawler.getVisionTowerMeal(
                        selectedDate
                );

        MealInfo educationMeal =
                mealCrawler.getEducationMeal(
                        selectedDate
                );

        MealInfo dormitoryMeal =
                mealCrawler.getDormitoryMeal(
                        selectedDate
                );

        MealInfo medicalMeal =
                mealCrawler.getMedicalMeal(
                        selectedDate
                );

        // ==========================================
        // Model
        // ==========================================

        model.addAttribute(
                "user",
                loginUser
        );

        model.addAttribute(
                "noticeList",
                noticeList
        );

        model.addAttribute(
                "visionMeal",
                visionMeal
        );

        model.addAttribute(
                "educationMeal",
                educationMeal
        );

        model.addAttribute(
                "dormitoryMeal",
                dormitoryMeal
        );

        model.addAttribute(
                "medicalMeal",
                medicalMeal
        );

        // 선택 날짜
        model.addAttribute(
                "selectedMealDate",
                selectedDate.toString()
        );

        model.addAttribute(
                "selectedMealDateLabel",
                selectedDate.format(
                        selectedDateFormatter
                )
        );

        // 주간 범위
        model.addAttribute(
                "mealWeekRange",
                mealWeekRange
        );

        // 이전 주
        model.addAttribute(
                "prevWeekDate",
                previousWeekDate.toString()
        );

        // 다음 주
        model.addAttribute(
                "nextWeekDate",
                nextWeekDate.toString()
        );

        // 월~일 날짜 버튼
        model.addAttribute(
                "mealWeekDays",
                mealWeekDays
        );

        return "main/dashboard";
    }
}