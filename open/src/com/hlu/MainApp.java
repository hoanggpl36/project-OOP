package com.hlu;

import com.hlu.crawler.*;
import com.hlu.model.SocialPost;
import com.hlu.analyzer.*;
import com.hlu.api.ApiClient;
import com.hlu.ui.MainDashboard;
import com.hlu.preprocessing.DataPreprocessor;

import javax.swing.*;
import java.util.*;

public class MainApp {

    public static void main(String[] args) {

        System.out.println("1. Đang thu thập dữ liệu từ file CSV gốc...");
        // Trỏ tới file data thật của bạn
        IDataFetcher fetcher = new CsvFetcher(
                "C:\\Users\\hoang\\Desktop\\HeThongBaoLu\\.vscode\\oop_project\\final_data.csv");

        Calendar cal = Calendar.getInstance();
        Date to = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date from = cal.getTime();

        List<SocialPost> posts = fetcher.fetchData("bão yagi", from, to);

        System.out.println("2. Đang phân tích dữ liệu qua Python API...");
        for (SocialPost post : posts) {
            // Tiền xử lý
            String cleanText = DataPreprocessor.cleanText(post.getContent());

            // Gọi API
            String res = ApiClient.analyze(cleanText);

            if ("ERROR".equals(res)) {
                // Fallback nếu chưa bật Python server
                post.setSentiment("Chưa phân tích (Lỗi API)");
            } else {
                post.setSentiment(ApiClient.getSentiment(res));
            }
        }

        System.out.println("3. Chạy các bài toán phân tích...");
        AnalysisTask task2 = new DamageClassificationTask();
        task2.execute(posts);

        AnalysisTask task1 = new SentimentOverTimeTask();
        task1.execute(posts);

        AnalysisTask task4 = new ReliefGoodsSentimentTask();
        task4.execute(posts);

        System.out.println("4. Khởi chạy giao diện Dashboard...");
        SwingUtilities.invokeLater(() -> {
            new MainDashboard(posts).setVisible(true);
        });
    }
}