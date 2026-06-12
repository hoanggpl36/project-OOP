package com.hlu.analyzer;

import com.hlu.api.ApiClient;

public class PythonApiAnalyzer implements ISentimentAnalyzer {

    @Override
    public String analyze(String text) {
        return ApiClient.analyze(text);
    }

    @Override
    public boolean needHelp(String rawResult) {
        return ApiClient.needHelp(rawResult);
    }

    @Override
    public String getSentiment(String rawResult) {
        return ApiClient.getSentiment(rawResult);
    }

    @Override
    public String getName() {
        return "Python BERT API (FastAPI)";
    }
}
