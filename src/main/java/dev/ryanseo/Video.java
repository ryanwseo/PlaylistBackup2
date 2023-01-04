package dev.ryanseo;

import com.google.gson.annotations.SerializedName;

public class Video {
    private String videoId;
    private String title;
    private String videoOwnerChannelId;
    private String videoOwnerChannelTitle;
    private PrivacyStatus privacyStatus;
    @SerializedName("url")
    private String thumbnailUrl;
    private boolean isAvailable;

    public Video(String videoId, String title, String videoOwnerChannelId, String videoOwnerChannelTitle, PrivacyStatus privacyStatus, String thumbnailUrl) {
        this.videoId = videoId;
        this.title = title;
        this.videoOwnerChannelId = videoOwnerChannelId;
        this.videoOwnerChannelTitle = videoOwnerChannelTitle;
        this.privacyStatus = privacyStatus;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Video() {
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoOwnerChannelId() {
        return videoOwnerChannelId;
    }

    public String getVideoOwnerChannelTitle() {
        return videoOwnerChannelTitle;
    }

    public PrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", videoOwnerChannelId='" + videoOwnerChannelId + '\'' +
                ", videoOwnerChannelTitle='" + videoOwnerChannelTitle + '\'' +
                ", privacyStatus=" + privacyStatus + '\'' +
                ", thumbnailUrl=" + thumbnailUrl +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Video) {
            return ((Video) obj).getVideoId().equals(this.getVideoId());
        } else {
            return false;
        }
    }
}
