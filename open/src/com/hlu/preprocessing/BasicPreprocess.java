package com.hlu.preprocessing;

public class BasicPreprocess implements PreprocessStrategy {
    @Override
    public String preprocess(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    @Override
    public String getName() {
        return "Cơ bản (Chữ thường & khoảng trắng)";
    }
}
