package com.hlu.preprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StopwordsPreprocess implements PreprocessStrategy {
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "thì", "là", "mà", "ở", "của", "với", "và", "nhưng", "cho", "để", "những"
    ));

    @Override
    public String preprocess(String text) {
        if (text == null) return "";
        // Sử dụng tiền xử lý nâng cao làm sạch trước
        String clean = new AdvancedPreprocess().preprocess(text);
        
        // Tách từ và loại bỏ từ dừng
        String[] words = clean.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!STOPWORDS.contains(word)) {
                sb.append(word).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public String getName() {
        return "Loại bỏ từ dừng (Stopwords)";
    }
}
