package com.hlu.analyzer;

import com.hlu.model.SocialPost;
import com.hlu.model.DisasterConfig;
import com.hlu.preprocessing.DataPreprocessor;

import java.util.List;

public class ReliefGoodsSentimentTask implements AnalysisTask {

    @Override
    public void execute(List<SocialPost> posts) {
        System.out.println("\n--- BÀI TOÁN 4: TÂM LÝ THEO TỪNG LOẠI HÀNG CỨU TRỢ ---");

        // Nhận diện hàng cứu trợ trước
        for (SocialPost post : posts) {
            String cleanText = DataPreprocessor.cleanText(post.getContent());
            String classifiedGood = "Không nhắc đến";

            if (cleanText.contains("thực phẩm") || cleanText.contains("đồ ăn") || cleanText.contains("nước")) {
                classifiedGood = "Thực phẩm";
            } else if (cleanText.contains("y tế") || cleanText.contains("thuốc") || cleanText.contains("bác sĩ")) {
                classifiedGood = "Y tế";
            } else if (cleanText.contains("tiền mặt") || cleanText.contains("tiền")) {
                classifiedGood = "Tiền mặt";
            } else if (cleanText.contains("nhà ở") || cleanText.contains("chỗ ở") || cleanText.contains("lều")) {
                classifiedGood = "Nhà ở";
            } else if (cleanText.contains("giao thông") || cleanText.contains("di chuyển") || cleanText.contains("thuyền")) {
                classifiedGood = "Giao thông";
            }

            post.setReliefGood(classifiedGood);
        }

        // Thống kê
        for (String good : DisasterConfig.RELIEF_GOODS) {
            int pos = 0, neg = 0;
            for (SocialPost post : posts) {
                if (good.equals(post.getReliefGood())) {
                    if ("Tích cực".equals(post.getSentiment())) pos++;
                    if ("Tiêu cực".equals(post.getSentiment())) neg++;
                }
            }
            System.out.println("Hàng cứu trợ: " + good + " -> Tích cực: " + pos + " | Tiêu cực: " + neg);
        }
    }
}
