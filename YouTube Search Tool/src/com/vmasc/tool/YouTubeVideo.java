package com.vmasc.tool;

import java.util.List;

public class YouTubeVideo {
 
     private List<String> thumbnails;
     private List<YouTubeMedia> medias;
     private String webPlayerUrl;
     private String embeddedWebPlayerUrl;
     private String title;
     private String description;
     private int duration;
     private String videoOwner;
     private String videoId;
 
     public List<String> getThumbnails() {
          return thumbnails;
     }
     public void setThumbnails(List<String> thumbnails) {
          this.thumbnails = thumbnails;
     }

     public List<YouTubeMedia> getMedias() {
          return medias;
     }
     public void setMedias(List<YouTubeMedia> medias) {
         this.medias = medias;
     }
 
     public String getWebPlayerUrl() {
          return webPlayerUrl;
     }
     public void setWebPlayerUrl(String webPlayerUrl) {
            this.webPlayerUrl = webPlayerUrl;
     }

     public String getEmbeddedWebPlayerUrl() {
          return embeddedWebPlayerUrl;
     }
     public void setEmbeddedWebPlayerUrl(String embeddedWebPlayerUrl) {
          this.embeddedWebPlayerUrl = embeddedWebPlayerUrl;
     }
 
     public String retrieveHttpLocation() {
          if (medias==null || medias.isEmpty()) {
               return null;
          }
          for (YouTubeMedia media : medias) {
               String location = media.getLocation();
               if (location.startsWith("http")) {
                    return location;
               }
          }
          return null;
      }
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getVideoOwner() {
		return videoOwner;
	}
	public void setVideoOwner(String videoOwner) {
		this.videoOwner = videoOwner;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

}
