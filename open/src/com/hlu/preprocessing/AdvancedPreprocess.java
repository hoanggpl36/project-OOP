package com.hlu.preprocessing;

public class AdvancedPreprocess implements PreprocessStrategy {
    @Override
    public String preprocess(String text) {
        if (text == null) return "";
        String clean = text.toLowerCase();
        // Loại bỏ ký tự đặc biệt, giữ lại chữ cái/số tiếng Việt
        clean = clean.replaceAll("[^a-zA-Z0-9àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ\\s]", "");
        clean = clean.replaceAll("\\s+", " ").trim();
        return clean;
    }

    @Override
    public String getName() {
        return "Nâng cao (Xóa ký tự đặc biệt)";
    }
}
