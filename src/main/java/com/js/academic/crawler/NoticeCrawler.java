package com.js.academic.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NoticeCrawler {

    private static final String NOTICE_URL = "https://www.gachon.ac.kr/kor/7986/subview.do";
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}[.\\-]\\d{1,2}[.\\-]\\d{1,2}");

    public List<Map<String, String>> getNoticeList() {
        List<Map<String, String>> noticeList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(NOTICE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .ignoreHttpErrors(true)
                    .get();

            Elements rows = doc.select(".board-table tbody tr, .artclTable tbody tr");

            for (Element row : rows) {
                Element titleEl = null;
                for (Element a : row.select("a[href]")) {
                    if (!a.text().trim().isEmpty()) {
                        titleEl = a;
                        break;
                    }
                }

                if (titleEl == null) continue;

                String rawHref = titleEl.attr("href");
                String title = titleEl.text().trim();
                
                // 자바스크립트 함수 안의 인자들 추출
                List<String> scriptArgs = new ArrayList<>();
                Matcher matcher = Pattern.compile("'([^']+)'").matcher(rawHref);
                while (matcher.find()) {
                    scriptArgs.add(matcher.group(1));
                }

                // 🔍 핵심 수정: 짧은 숫자가 아니라, 길이가 긴 진짜 암호화 토큰(20자 이상)을 찾아냄!
                String encToken = "";
                for (String arg : scriptArgs) {
                    if (arg.length() > 20) { // 암호화된 토큰은 길이가 깁니다.
                        encToken = arg;
                        break;
                    }
                }
                
                // 만약 긴 토큰을 못 찾았다면 마지막 인자를 대안으로 사용
                if (encToken.isEmpty() && !scriptArgs.isEmpty()) {
                    encToken = scriptArgs.get(scriptArgs.size() - 1);
                }

                // 🔗 진짜 상세 페이지로 연결되는 완성된 URL 조립
                String link = "";
                if (!encToken.isEmpty()) {
                    link = NOTICE_URL + "?enc=" + encToken;
                } else {
                    link = NOTICE_URL; 
                }
                
                // 날짜 추출
                String date = "";
                for (Element td : row.select("td")) {
                    Matcher m = DATE_PATTERN.matcher(td.text().trim());
                    if (m.find()) {
                        date = m.group();
                        break;
                    }
                }

                Map<String, String> notice = new HashMap<>();
                notice.put("title", title);
                notice.put("link", link); 
                notice.put("date", date);
                notice.put("articleId", encToken);

                noticeList.add(notice);

                if (noticeList.size() >= 5) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return noticeList;
    }

    public Map<String, String> getNoticeDetail(String articleId) {
        Map<String, String> noticeDetail = new HashMap<>();
        noticeDetail.put("title", "상세 페이지");
        noticeDetail.put("content", "원문 페이지로 이동합니다.");
        noticeDetail.put("date", "");
        return noticeDetail;
    }
}