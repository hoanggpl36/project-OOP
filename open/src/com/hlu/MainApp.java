package com.hlu;

import com.hlu.crawler.*;
import com.hlu.model.SocialPost;
import com.hlu.analyzer.*;
import com.hlu.api.ApiClient;
import com.hlu.ui.MainDashboard;
import com.hlu.ui.DashboardController;
import com.hlu.preprocessing.DataPreprocessor;

import javax.swing.*;
import java.util.*;

public class MainApp {

    public static void main(String[] args) {
        // Cấu hình giao diện FlatLaf hiện đại
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.out.println("Không khởi tạo được FlatLaf: " + e.getMessage());
        }

        System.out.println("1. Đang thu thập dữ liệu từ file CSV gốc...");
        // Sử dụng Factory Pattern để tạo fetcher
        IDataFetcher fetcher = DataFetcherFactory.getFetcher("CSV", 
                "C:\\Users\\hoang\\Desktop\\HeThongBaoLu\\.vscode\\oop_project\\final_data.csv");

        Calendar cal = Calendar.getInstance();
        Date to = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date from = cal.getTime();

        List<SocialPost> posts = fetcher.fetchData("bão yagi", from, to);

        System.out.println("2. Đang phân tích dữ liệu khởi tạo...");
        com.hlu.preprocessing.PreprocessStrategy preprocessor = new com.hlu.preprocessing.AdvancedPreprocess();
        ISentimentAnalyzer analyzer = new PythonApiAnalyzer();

        for (SocialPost post : posts) {
            String cleanText = preprocessor.preprocess(post.getContent());
            String res = analyzer.analyze(cleanText);

            if ("ERROR".equals(res)) {
                // Tự động fallback về bộ phân tích cục bộ của Java nếu Python API bị tắt/lỗi
                ISentimentAnalyzer fallbackAnalyzer = new JavaLocalAnalyzer();
                String fallbackRes = fallbackAnalyzer.analyze(cleanText);
                post.setSentiment(fallbackAnalyzer.getSentiment(fallbackRes) + " (Offline Fallback)");
            } else {
                post.setSentiment(analyzer.getSentiment(res));
            }
        }

        // Tạo Controller cho MVC
        DashboardController controller = new DashboardController(posts, fetcher);

        System.out.println("3. Chạy các bài toán phân tích ban đầu...");
        controller.reRunAnalysis();

        System.out.println("4. Khởi chạy giao diện Dashboard...");
        SwingUtilities.invokeLater(() -> {
            new MainDashboard(controller).setVisible(true);
        });
    }
}