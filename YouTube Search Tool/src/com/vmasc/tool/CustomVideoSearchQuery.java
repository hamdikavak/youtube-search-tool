package com.vmasc.tool;


public class CustomVideoSearchQuery {
	
	private boolean HDOnly;
	private VideoLengthEnum VideoLength;
	
	public boolean isHDOnly() {
		return HDOnly;
	}
	public void setHDOnly(boolean hDOnly) {
		HDOnly = hDOnly;
	}
	public VideoLengthEnum getVideoLength() {
		return VideoLength;
	}
	public void setVideoLength(VideoLengthEnum videoLength) {
		VideoLength = videoLength;
	}
}
