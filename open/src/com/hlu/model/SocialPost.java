package com.hlu.model;

import java.util.Date;

public class SocialPost {

    private String id;
    private String source;
    private String content;
    private Date timestamp;
    private String sentiment;
    private String damageType;
    private String reliefGood;

    public SocialPost(String id, String source, String content,
                      Date timestamp, String sentiment, String damageType, String reliefGood) {
        this.id = id;
        this.source = source;
        this.content = content;
        this.timestamp = timestamp;
        this.sentiment = sentiment;
        this.damageType = damageType;
        this.reliefGood = reliefGood;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getReliefGood() {
        return reliefGood;
    }

    public void setReliefGood(String reliefGood) {
        this.reliefGood = reliefGood;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getSentiment() {
        return sentiment;
    }
    public void setSentiment(String sentiment) {
    this.sentiment = sentiment;
}

    public String getDamageType() {
        return damageType;
    }
   
}