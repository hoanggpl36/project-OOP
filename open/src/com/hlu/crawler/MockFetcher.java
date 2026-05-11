package com.hlu.crawler;

import com.hlu.model.SocialPost;
import com.hlu.model.DisasterConfig;

import java.util.*;

public class MockFetcher implements IDataFetcher {

    @Override
    public List<SocialPost> fetchData(String keyword, Date from, Date to) {
        List<SocialPost> list = new ArrayList<>();
        Random random = new Random();

        String[] sources = {"Facebook", "Twitter", "TikTok", "Youtube"};
        String[] templates = {
            "Nhà cửa bị tốc mái nhiều quá, bão thật khủng khiếp.",
            "Giao thông tê liệt hoàn toàn, nước ngập sâu.",
            "Đã nhận được thực phẩm và nước sạch từ đoàn cứu trợ.",
            "Cần hỗ trợ y tế khẩn cấp tại khu vực bị cô lập.",
            "Nhiều tài sản bị nước cuốn trôi, thiệt hại kinh tế rất lớn.",
            "Chính quyền đang phân phát tiền mặt hỗ trợ bà con xây lại nhà.",
            "Sạt lở đất làm hỏng cơ sở hạ tầng giao thông.",
            "Rất thất vọng vì công tác cứu hộ diễn ra quá chậm.",
            "Tuyệt vời, quân đội đã đến hỗ trợ sơ tán người dân an toàn."
        };

        long fromMs = from.getTime();
        long toMs = to.getTime();
        if (fromMs >= toMs) {
            toMs = fromMs + 5 * 24 * 60 * 60 * 1000L; // Default 5 days
        }

        // Tạo ra 100 bài đăng ngẫu nhiên
        for (int i = 1; i <= 100; i++) {
            String text = templates[random.nextInt(templates.length)];
            String source = sources[random.nextInt(sources.length)];
            
            // Random timestamp
            long randomTime = fromMs + (long) (random.nextDouble() * (toMs - fromMs));
            Date date = new Date(randomTime);

            // Mặc định giá trị rỗng trước, sau này Analyzer sẽ phân loại
            list.add(new SocialPost(
                    String.valueOf(i),
                    source,
                    text,
                    date,
                    "Chưa phân tích", // Sentiment
                    "Chưa phân loại", // Damage Type
                    "Chưa phân loại"  // Relief Good
            ));
        }

        // Sort by date ascending
        list.sort(Comparator.comparing(SocialPost::getTimestamp));
        
        return list;
    }
}