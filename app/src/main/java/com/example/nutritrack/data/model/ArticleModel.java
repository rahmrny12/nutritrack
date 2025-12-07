package com.example.nutritrack.data.model;

import java.io.Serializable;

public class ArticleModel implements Serializable {
    public String id;
    public String userId;
    public String title;
    public String slug;
    public String content;
    public String status;
    public String imageUrl;
    public String createdAt;
    public String updatedAt;
}
