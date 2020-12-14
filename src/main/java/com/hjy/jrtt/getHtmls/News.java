package com.hjy.jrtt.getHtmls;

import java.util.ArrayList;

public class News {
    String title;
    String url;
    String content;
    ArrayList<String> comments;

    public News(String title, String url, String content, ArrayList<String> comments) {
        this.title = (title != null) ? title : "";
        this.url = (url != null) ? url : "";
        this.content = (content != null) ? content : "";
        this.comments = (comments != null) ? comments : new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
