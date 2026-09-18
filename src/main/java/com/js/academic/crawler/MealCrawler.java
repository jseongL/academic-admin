package com.js.academic.crawler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class MealCrawler {

    // 비전타워 식당
    private static final String VISION_TOWER_URL =
            "https://www.gachon.ac.kr/kor/7347/subview.do";

    // 교육대학원 식당
    private static final String EDUCATION_URL =
            "https://www.gachon.ac.kr/kor/7349/subview.do";

    // 학생생활관 식당
    private static final String DORMITORY_URL =
            "https://www.gachon.ac.kr/kor/7350/subview.do";

    // 체육관(메디컬) 식당
    private static final String MEDICAL_URL =
            "https://www.gachon.ac.kr/kor/7351/subview.do";

    // 날짜 형식 : 2026.09.18
    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");

    // 가격 형식 : 5500원 / 5,500원
    private static final Pattern PRICE_PATTERN =
            Pattern.compile("(\\d{1,3}(?:,\\d{3})+|\\d{4,5})\\s*원");


    // =========================================================
    // 오늘 날짜 기준 식단 조회
    // =========================================================

    public MealInfo getVisionTowerTodayMeal() {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        return getVisionTowerMeal(today);
    }


    public MealInfo getEducationTodayMeal() {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        return getEducationMeal(today);
    }


    public MealInfo getDormitoryTodayMeal() {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        return getDormitoryMeal(today);
    }


    public MealInfo getMedicalTodayMeal() {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Seoul")
                );

        return getMedicalMeal(today);
    }


    // =========================================================
    // 특정 날짜 기준 식단 조회
    // =========================================================

    public MealInfo getVisionTowerMeal(
            LocalDate date) {

        return getMeal(
                "비전타워",
                VISION_TOWER_URL,
                date,
                "점심 B메뉴"
        );
    }


    public MealInfo getEducationMeal(
            LocalDate date) {

        return getMeal(
                "교육대학원",
                EDUCATION_URL,
                date,
                "점심"
        );
    }


    public MealInfo getDormitoryMeal(
            LocalDate date) {

        return getMeal(
                "학생생활관",
                DORMITORY_URL,
                date,
                "점심"
        );
    }


    public MealInfo getMedicalMeal(
            LocalDate date) {

        return getMeal(
                "체육관(메디컬)",
                MEDICAL_URL,
                date,
                "점심"
        );
    }


    // =========================================================
    // 공통 식단 크롤링
    // =========================================================

    private MealInfo getMeal(
            String restaurantName,
            String url,
            LocalDate targetDate,
            String targetMealType) {

        String targetDateString =
                targetDate.format(
                        DateTimeFormatter.ofPattern(
                                "yyyy.MM.dd"
                        )
                );

        String displayDate =
                targetDate.format(
                        DateTimeFormatter.ofPattern(
                                "yyyy.MM.dd(E)",
                                Locale.KOREAN
                        )
                );


        try {

            Document doc =
                    Jsoup.connect(url)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/140.0.0.0 Safari/537.36"
                            )
                            .referrer(
                                    "https://www.gachon.ac.kr/"
                            )
                            .timeout(15000)
                            .followRedirects(true)
                            .ignoreHttpErrors(true)
                            .get();


            // 식단 테이블 찾기
            Element mealTable =
                    findMealTable(doc);


            // 식단 테이블이 없으면 빈 결과 반환
            if (mealTable == null) {

                return emptyMeal(
                        displayDate,
                        targetMealType
                );
            }


            Elements rows =
                    mealTable.select(
                            "tbody tr"
                    );


            String currentDate = "";


            // =================================================
            // 식단표 행 반복
            // =================================================

            for (Element row : rows) {

                List<Element> cells =
                        getDirectCells(row);


                if (cells.size() < 2) {

                    continue;
                }


                // =============================================
                // 현재 행에 날짜가 있으면 저장
                // =============================================

                for (Element cell : cells) {

                    Matcher matcher =
                            DATE_PATTERN.matcher(
                                    cell.text()
                            );


                    if (matcher.find()) {

                        currentDate =
                                matcher.group();

                        break;
                    }
                }


                // =============================================
                // 선택한 날짜가 아니면 넘어감
                // =============================================

                if (!targetDateString.equals(
                        currentDate)) {

                    continue;
                }


                // =============================================
                // 원하는 식단 종류 찾기
                //
                // 비전타워 : 점심 B메뉴
                // 교육대학원 : 점심
                // 학생생활관 : 점심
                // 메디컬 : 점심
                // =============================================

                int mealTypeIndex = -1;


                for (int i = 0;
                     i < cells.size();
                     i++) {

                    String cellText =
                            cells.get(i)
                                    .text()
                                    .trim();


                    if (cellText.startsWith(
                            targetMealType)) {

                        mealTypeIndex = i;

                        break;
                    }
                }


                if (mealTypeIndex == -1) {

                    continue;
                }


                // 식단구분 다음 셀이 식단내용
                int menuIndex =
                        mealTypeIndex + 1;


                if (menuIndex >= cells.size()) {

                    continue;
                }


                String mealTypeText =
                        cells.get(
                                mealTypeIndex
                        )
                        .text()
                        .trim();


                Element menuCell =
                        cells.get(
                                menuIndex
                        );


                // 가격 추출
                String price =
                        extractPrice(
                                mealTypeText
                        );


                // 식단 구분에서 가격 제거
                String mealType =
                        removePrice(
                                mealTypeText
                        );


                // 실제 메뉴 목록 추출
                List<String> menus =
                        extractMenuLines(
                                menuCell
                        );


                // =============================================
                // 메뉴를 찾았으면 MealInfo 반환
                // =============================================

                if (!menus.isEmpty()) {

                    return new MealInfo(
                            displayDate,
                            mealType,
                            price,
                            menus,
                            true
                    );
                }
            }


        } catch (Exception e) {

            /*
             * 평상시에는 아무 로그도 출력하지 않고,
             * 실제 오류가 발생했을 때만 오류 출력
             */
            System.err.println(
                    "[MealCrawler] "
                    + restaurantName
                    + " 식단 크롤링 실패"
            );

            e.printStackTrace();
        }


        // 해당 날짜에 식단이 없는 경우
        return emptyMeal(
                displayDate,
                targetMealType
        );
    }


    // =========================================================
    // 식단표 테이블 찾기
    // =========================================================

    private Element findMealTable(
            Document doc) {

        for (Element table :
                doc.select("table")) {

            String tableText =
                    table.text();


            if (tableText.contains(
                    "식단구분")
                    &&
                tableText.contains(
                    "식단내용")) {

                return table;
            }
        }


        return null;
    }


    // =========================================================
    // 현재 tr 바로 아래의 th / td 가져오기
    // =========================================================

    private List<Element> getDirectCells(
            Element row) {

        List<Element> cells =
                new ArrayList<>();


        for (Element child :
                row.children()) {

            if ("td".equalsIgnoreCase(
                    child.tagName())
                    ||
                "th".equalsIgnoreCase(
                    child.tagName())) {

                cells.add(child);
            }
        }


        return cells;
    }


    // =========================================================
    // 메뉴 셀에서 메뉴를 한 줄씩 추출
    // =========================================================

    private List<String> extractMenuLines(
            Element menuCell) {

        List<String> menus =
                new ArrayList<>();


        Element clone =
                menuCell.clone();


        // <br> 태그를 줄바꿈으로 변환
        for (Element br :
                clone.select("br")) {

            br.after("\n");

            br.remove();
        }


        // div, p, li 태그도 줄바꿈 처리
        for (Element block :
                clone.select(
                        "div, p, li")) {

            block.append("\n");
        }


        String text =
                clone.wholeText();


        String[] lines =
                text.split("\\R");


        for (String line :
                lines) {

            String menu =
                    line.trim();


            if (menu.isEmpty()) {

                continue;
            }


            // 데이터 없음 문구 제외
            if (menu.contains(
                    "등록된 식단내용")) {

                continue;
            }


            // "-" 제외
            if ("-".equals(menu)) {

                continue;
            }


            // 중복 메뉴 제외
            if (!menus.contains(menu)) {

                menus.add(menu);
            }
        }


        // =====================================================
        // 줄바꿈이 제대로 분리되지 않은 경우
        // 자식 HTML 태그를 기준으로 다시 추출
        // =====================================================

        if (menus.size() <= 1) {

            List<String> childMenus =
                    new ArrayList<>();


            for (Element child :
                    menuCell.children()) {

                String value =
                        child.text()
                                .trim();


                if (value.isEmpty()) {

                    continue;
                }


                if (!childMenus.contains(
                        value)) {

                    childMenus.add(value);
                }
            }


            if (!childMenus.isEmpty()) {

                menus =
                        childMenus;
            }
        }


        return menus;
    }


    // =========================================================
    // 가격 추출
    //
    // 5500원
    // →
    // 5,500원
    // =========================================================

    private String extractPrice(
            String text) {

        Matcher matcher =
                PRICE_PATTERN.matcher(
                        text
                );


        if (!matcher.find()) {

            return "";
        }


        String rawPrice =
                matcher.group(1)
                        .replace(",", "");


        try {

            int price =
                    Integer.parseInt(
                            rawPrice
                    );


            return String.format(
                    "%,d원",
                    price
            );


        } catch (
                NumberFormatException e) {

            return matcher.group();
        }
    }


    // =========================================================
    // 식단 구분에서 가격 제거
    //
    // 점심 B메뉴(단품) 5500원
    // →
    // 점심 B메뉴(단품)
    //
    // 점심(6,000원)
    // →
    // 점심
    // =========================================================

    private String removePrice(
            String text) {

        String result =
                PRICE_PATTERN
                        .matcher(text)
                        .replaceAll("");


        // 가격 제거 후 ()만 남는 경우 제거
        result =
                result.replaceAll(
                        "\\(\\s*\\)",
                        ""
                );


        return result
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }


    // =========================================================
    // 식단이 없는 경우
    // =========================================================

    private MealInfo emptyMeal(
            String date,
            String mealType) {

        return new MealInfo(
                date,
                mealType,
                "",
                new ArrayList<>(),
                false
        );
    }
}