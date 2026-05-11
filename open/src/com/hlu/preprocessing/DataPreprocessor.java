package com.hlu.preprocessing;

import com.hlu.model.SocialPost;
import java.util.List;

public class DataPreprocessor {

    public static void preprocess(List<SocialPost> posts) {
        for (SocialPost post : posts) {
            String content = post.getContent();
            
            // 1. Chuyển thành chữ thường
            content = content.toLowerCase();
            
            // 2. Xóa các ký tự đặc biệt, giữ lại chữ cái và số tiếng Việt
            content = content.replaceAll("[^a-zA-Z0-9àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ\\s]", "");
            
            // 3. Xóa khoảng trắng thừa
            content = content.replaceAll("\\s+", " ").trim();
            
            // (Tuỳ chọn) Ở đây có thể thêm logic xóa Stopwords
            
            // Cập nhật lại nội dung (hoặc tạo trường preprocessedContent trong SocialPost nếu cần giữ bản gốc)
            // Trong thiết kế hiện tại, mình ghi đè luôn cho đơn giản hoặc bạn có thể lưu vào biến khác
            // post.setContent(content); 
            // Tuy nhiên SocialPost không có setContent, nên ta giữ nguyên hoặc chỉ dùng preprocess text trước khi gửi API
        }
    }
    
    public static String cleanText(String text) {
        if (text == null) return "";
        String clean = text.toLowerCase();
        clean = clean.replaceAll("[^a-zA-Z0-9àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ\\s]", "");
        clean = clean.replaceAll("\\s+", " ").trim();
        return clean;
    }
}
