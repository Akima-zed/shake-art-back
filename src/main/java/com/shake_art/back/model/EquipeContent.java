package com.shake_art.back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "equipe_content")
public class EquipeContent {
    @Id
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String presentationText;
    private String bannerImageUrl;
    public EquipeContent() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPresentationText() { return presentationText; }
    public void setPresentationText(String presentationText) { this.presentationText = presentationText; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }
}
