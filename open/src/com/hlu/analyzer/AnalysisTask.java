package com.hlu.analyzer;

import com.hlu.model.SocialPost;
import java.util.List;

public interface AnalysisTask {

    void execute(List<SocialPost> posts);

}