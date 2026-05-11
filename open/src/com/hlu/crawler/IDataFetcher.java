package com.hlu.crawler;

import com.hlu.model.SocialPost;
import java.util.List;
import java.util.Date;

public interface IDataFetcher {

    List<SocialPost> fetchData(String keyword, Date from, Date to);

}