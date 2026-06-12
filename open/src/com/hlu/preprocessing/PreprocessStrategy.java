package com.hlu.preprocessing;

public interface PreprocessStrategy {
    String preprocess(String text);
    String getName();
}
