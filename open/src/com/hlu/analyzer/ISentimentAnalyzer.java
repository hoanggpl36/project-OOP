package com.hlu.analyzer;

public interface ISentimentAnalyzer {
    String analyze(String text);
    boolean needHelp(String rawResult);
    String getSentiment(String rawResult);
    String getName();
}
