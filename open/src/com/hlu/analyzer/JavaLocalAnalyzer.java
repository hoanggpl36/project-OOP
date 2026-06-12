package com.hlu.analyzer;

public class JavaLocalAnalyzer implements ISentimentAnalyzer {

    @Override
    public String analyze(String text) {
        if (text == null) return "NEUTRAL";
        String lower = text.toLowerCase();

        // Kiểm tra xem có từ phủ định hoặc phủ quyết việc cần cứu trợ khẩn cấp không
        boolean hasNegation = lower.contains("không cần") || lower.contains("không bị") || 
                             lower.contains("không sao") || lower.contains("chưa cần") ||
                             lower.contains("không gặp") || lower.contains("ổn") ||
                             lower.contains("bình thường") || lower.contains("không thiệt hại") ||
                             lower.contains("không phải") || lower.contains("đã được") ||
                             lower.contains("đã cứu") || lower.contains("hết ngập") ||
                             lower.contains("hết lũ") || lower.contains("rút rồi") ||
                             lower.contains("an toàn");

        boolean isNegative = (lower.contains("cứu") || lower.contains("hỏng") || lower.contains("thiếu") ||
                             lower.contains("ngập") || lower.contains("sạt lở") || lower.contains("cuốn trôi") || 
                             lower.contains("mắc kẹt") || lower.contains("nguy hiểm") ||
                             lower.contains("help") || lower.contains("emergency") || lower.contains("sos")) && !hasNegation;

        boolean isPositive = lower.contains("cảm ơn") || lower.contains("tốt") || lower.contains("an toàn") ||
                             lower.contains("nhận được") || lower.contains("giúp đỡ") || lower.contains("tuyệt vời") ||
                             hasNegation; // Các câu phủ định thiệt hại/nhu cầu khẩn cấp thường là an toàn/trung lập/tích cực

        if (isNegative) {
            return "NEGATIVE_NEED_HELP";
        } else if (isPositive) {
            return "POSITIVE";
        }
        return "NEUTRAL";
    }

    @Override
    public boolean needHelp(String rawResult) {
        return "NEGATIVE_NEED_HELP".equals(rawResult);
    }

    @Override
    public String getSentiment(String rawResult) {
        if ("NEGATIVE_NEED_HELP".equals(rawResult)) return "Tiêu cực";
        if ("POSITIVE".equals(rawResult)) return "Tích cực";
        return "Trung lập";
    }

    @Override
    public String getName() {
        return "Java Local Rules (Offline)";
    }
}
