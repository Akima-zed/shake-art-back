package com.shake_art.back.equipe;

public class EquipeContent {
    private Long id;
    private String presentationText;
    private String bannerImageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPresentationText() {
        return presentationText;
    }

    public void setPresentationText(String presentationText) {
        this.presentationText = presentationText;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }
}
