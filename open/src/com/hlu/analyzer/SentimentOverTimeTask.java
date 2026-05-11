package com.hlu.analyzer;

import com.hlu.model.SocialPost;
import java.text.SimpleDateFormat;
import java.util.*;

public class SentimentOverTimeTask implements AnalysisTask {

    @Override
    public void execute(List<SocialPost> posts) {
        System.out.println("\n--- BÀI TOÁN 1: THEO DÕI TÂM LÝ THEO THỜI GIAN ---");
        
        // Map: Ngày (String) -> (Tích cực, Tiêu cực)
        Map<String, int[]> sentimentByDate = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (SocialPost post : posts) {
            String dateStr = sdf.format(post.getTimestamp());
            sentimentByDate.putIfAbsent(dateStr, new int[]{0, 0}); // index 0: Tích cực, 1: Tiêu cực

            if ("Tích cực".equals(post.getSentiment())) {
                sentimentByDate.get(dateStr)[0]++;
            } else if ("Tiêu cực".equals(post.getSentiment())) {
                sentimentByDate.get(dateStr)[1]++;
            }
        }

        for (Map.Entry<String, int[]> entry : sentimentByDate.entrySet()) {
            System.out.println("Ngày " + entry.getKey() + ": " 
                    + entry.getValue()[0] + " Tích cực | " 
                    + entry.getValue()[1] + " Tiêu cực");
        }
    }
}
