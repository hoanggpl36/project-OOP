package com.hlu.analyzer;

import com.hlu.model.SocialPost;
import com.hlu.model.DisasterConfig;
import com.hlu.preprocessing.DataPreprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageClassificationTask implements AnalysisTask {

    @Override
    public void execute(List<SocialPost> posts) {
        System.out.println("--- BÀI TOÁN 2: PHÂN LOẠI THIỆT HẠI ---");

        Map<String, Integer> damageCounts = new HashMap<>();
        for (String type : DisasterConfig.DAMAGE_TYPES) {
            damageCounts.put(type, 0);
        }

        for (SocialPost post : posts) {
            String cleanText = DataPreprocessor.cleanText(post.getContent());
            String classifiedType = "Khác";

            // Nếu bài viết mang ý nghĩa Tích cực (như cảm ơn, giúp đỡ) thì thường không phải báo cáo thiệt hại
            boolean isHelpfulOrPositive = post.getSentiment().contains("Tích cực") || 
                                          post.getSentiment().equals("POSITIVE") ||
                                          cleanText.contains("giúp đỡ") ||
                                          cleanText.contains("hỗ trợ");

            if (!isHelpfulOrPositive) {
                // Logic phân loại thiệt hại chặt chẽ hơn
                if (cleanText.contains("nhà") || cleanText.contains("tốc mái") || cleanText.contains("sập")) {
                    classifiedType = "Nhà cửa bị hư hỏng";
                } else if (cleanText.contains("giao thông") || cleanText.contains("đường") || cleanText.contains("ngập") || cleanText.contains("sạt lở")) {
                    classifiedType = "Cơ sở hạ tầng";
                } else if (cleanText.contains("kinh tế") || cleanText.contains("tài sản") || cleanText.contains("cuốn trôi")) {
                    classifiedType = "Tài sản bị mất";
                } else if (cleanText.contains("thương vong") || cleanText.contains("sơ tán") || cleanText.contains("bị thương") || cleanText.contains("mất tích") || cleanText.contains("mắc kẹt")) {
                    classifiedType = "Người bị ảnh hưởng";
                }
            } else {
                classifiedType = "Hoạt động cứu trợ"; // Hoặc có thể để là Khác
            }

            post.setDamageType(classifiedType);
            
            // Chỉ thống kê các loại nằm trong danh sách thiệt hại ban đầu
            if (DisasterConfig.DAMAGE_TYPES.contains(classifiedType)) {
                damageCounts.put(classifiedType, damageCounts.getOrDefault(classifiedType, 0) + 1);
            }
        }

        System.out.println("Thống kê số lượng thiệt hại phổ biến:");
        damageCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue() + " bài viết"));
    }
}