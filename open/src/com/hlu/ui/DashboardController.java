package com.hlu.ui;

import com.hlu.model.SocialPost;
import com.hlu.crawler.IDataFetcher;
import com.hlu.crawler.CsvFetcher;
import com.hlu.api.ApiClient;
import com.hlu.preprocessing.DataPreprocessor;
import com.hlu.preprocessing.PreprocessStrategy;
import com.hlu.preprocessing.AdvancedPreprocess;
import com.hlu.analyzer.AnalysisTask;
import com.hlu.analyzer.DamageClassificationTask;
import com.hlu.analyzer.ReliefGoodsSentimentTask;
import com.hlu.analyzer.SentimentOverTimeTask;
import com.hlu.analyzer.ISentimentAnalyzer;
import com.hlu.analyzer.PythonApiAnalyzer;

import java.util.List;
import java.util.Date;

public class DashboardController {
    private List<SocialPost> posts;
    private IDataFetcher fetcher;

    public DashboardController(List<SocialPost> posts, IDataFetcher fetcher) {
        this.posts = posts;
        this.fetcher = fetcher;
    }

    public List<SocialPost> getPosts() {
        return posts;
    }

    public void reRunAnalysis() {
        new DamageClassificationTask().execute(posts);
        new ReliefGoodsSentimentTask().execute(posts);
        new SentimentOverTimeTask().execute(posts);
    }

    public SocialPost processNewPost(String text) throws Exception {
        return processNewPost(text, new AdvancedPreprocess(), new PythonApiAnalyzer());
    }

    public SocialPost processNewPost(String text, PreprocessStrategy preprocessor, ISentimentAnalyzer analyzer) throws Exception {
        String cleanText = preprocessor.preprocess(text);
        SocialPost newPost = new SocialPost(
                "U" + System.currentTimeMillis(),
                "Nhập tay",
                text,
                new Date(),
                "Chưa phân tích",
                "Chưa phân loại",
                "Chưa phân loại"
        );

        String res = analyzer.analyze(cleanText);
        boolean isError = "ERROR".equals(res);
        if (!isError) {
            newPost.setSentiment(analyzer.getSentiment(res));
        } else {
            newPost.setSentiment("Lỗi phân tích");
        }

        posts.add(newPost);

        // Chạy các phân tích để cập nhật nhãn cho bài post mới
        reRunAnalysis();

        // Ghi vào file CSV để lưu trữ lâu dài
        if (fetcher instanceof CsvFetcher) {
            boolean needHelp = !isError && analyzer.needHelp(res);
            ((CsvFetcher) fetcher).savePostToCsv(newPost, needHelp);
        }

        return newPost;
    }
}
