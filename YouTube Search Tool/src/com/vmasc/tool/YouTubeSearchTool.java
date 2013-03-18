package com.vmasc.tool;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.client.Query;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeQuery.OrderBy;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Person;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;

public class YouTubeSearchTool {
	
	/*
	 *Constants 
	 * */

	private final static String API_URL = "http://gdata.youtube.com/feeds/api/videos";
    private final static String YOUTUBE_EMBEDDED_URL = "http://www.youtube.com/v/";
	
    /**
     * Search parameters/settings
     */
    private final static int TIMEOUT = 20000;
	private final static int NUM_OF_RESULTS = 50;
	private final static YouTubeQuery.OrderBy ORDER_BY = OrderBy.RELEVANCE;
	private final static YouTubeQuery.SafeSearch SAFE_SEARCH = YouTubeQuery.SafeSearch.NONE;
	
	private ArrayList<String> keywords;
	private Hashtable<String, ArrayList<YouTubeVideo>> searchResults;
	
	public YouTubeSearchTool(){
		resetVariables();
	}

	private void resetVariables() {
		keywords = new ArrayList<String>();
		searchResults = new Hashtable<String, ArrayList<YouTubeVideo>>();
	}
	
	public void AddKeyword(String keyword){
		keywords.add(keyword);
	}
	
	public void AddKeywords(List<String> keywords){
		keywords.addAll(keywords);
	}
	
	public void AddKeywords(List<String> keywords, boolean overwrite){
		if(overwrite){
			keywords.clear();
		}
		keywords.addAll(keywords);
	}
	
	
	public void ClearKeywords(){
		keywords.clear();
	}
	
	public void DeleteKeywordByIndex(int index){
		keywords.remove(index);
	}
	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void search(CustomVideoSearchQuery query) {
		
		YouTubeService service = new YouTubeService("");
		YouTubeQuery ytQuery = null;
		ArrayList<YouTubeVideo> tempList;
		
        service.setConnectTimeout(TIMEOUT);
        searchResults.clear();
        
        try{
			for(int i=0; i < keywords.size(); i++ ){
				
				tempList = new ArrayList<YouTubeVideo>();

				for(int index=1;index<952;index=index+50){
					
			        ytQuery = new YouTubeQuery(new URL(API_URL));
			        ytQuery.setOrderBy(ORDER_BY);
			        ytQuery.setFullTextQuery(keywords.get(i));
			        ytQuery.setSafeSearch(SAFE_SEARCH);
			        ytQuery.setMaxResults(NUM_OF_RESULTS);
			        ytQuery.setStartIndex(index);
			        
			        
			        if(query.getVideoLength() == VideoLengthEnum.SHORT){
			        	ytQuery.addCustomParameter(new Query.CustomParameter("duration", "short"));
			        }
			        else if(query.getVideoLength() == VideoLengthEnum.LONG){
			        	ytQuery.addCustomParameter(new Query.CustomParameter("duration", "long"));
			        }
			        
			        if(query.isHDOnly()){
			        	ytQuery.addCustomParameter(new Query.CustomParameter("hd", "true"));
			        }
			
			        VideoFeed videoFeed = service.query(ytQuery, VideoFeed.class);  
			        List<VideoEntry> videoEntries = videoFeed.getEntries();
			        
			        tempList.addAll( convertEntries(videoEntries) );
				}
				searchResults.put(keywords.get(i), tempList);
			}
        }
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private List<YouTubeVideo> convertEntries(List<VideoEntry> videoEntries) {
		  
        List<YouTubeVideo> youtubeVideosList = new ArrayList<YouTubeVideo>();
        int duration, qIndex;
        String personName="", query = "?v=", webPlayerUrl, embeddedWebPlayerUrl;
        List<String> thumbnails;
        List<YouTubeMedia> medias;
        String[] idParts;
        
        for (VideoEntry videoEntry : videoEntries) {
        	
        	YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
        	
        	try{
        		duration = mediaGroup.getYouTubeContents().get(0).getDuration();
        	}
        	catch(Exception e){
        		duration=0;
        	}
        	//isRelated = true;//checkContext(videoEntry.getTitle().getPlainText(), mediaGroup.getDescription().getPlainTextContent());
	            
	        YouTubeVideo ytv = new YouTubeVideo();
		    personName="";
		    
		    for(Person p: videoEntry.getAuthors()){
		    	personName += p.getName();
		            break;
		    }
	            
		    webPlayerUrl = mediaGroup.getPlayer().getUrl();
		    ytv.setWebPlayerUrl(webPlayerUrl);
		   
		    ytv.setDuration( duration );
		    ytv.setDescription(mediaGroup.getDescription().getPlainTextContent());
		    ytv.setVideoOwner(personName);
		            
		    qIndex = webPlayerUrl.indexOf(query);
	    
	    
		    embeddedWebPlayerUrl = webPlayerUrl.substring(qIndex + query.length());
		    embeddedWebPlayerUrl = YOUTUBE_EMBEDDED_URL + embeddedWebPlayerUrl;
		    ytv.setEmbeddedWebPlayerUrl(embeddedWebPlayerUrl);
	   
	        thumbnails = new LinkedList<String>();

	        for (MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
	        	thumbnails.add(mediaThumbnail.getUrl());
	        }   
	        
	        ytv.setThumbnails(thumbnails);
	            
	        medias = new LinkedList<YouTubeMedia>();
	            
	        for (YouTubeMediaContent mediaContent : mediaGroup.getYouTubeContents()) {
	        	medias.add(new YouTubeMedia(mediaContent.getUrl(), mediaContent.getType()));
	        }
	        
	        ytv.setMedias(medias);
	        ytv.setTitle(videoEntry.getTitle().getPlainText());
	        
	        idParts = videoEntry.getId().split(":");
	        
	        ytv.setVideoId(idParts[idParts.length-1]);
	        youtubeVideosList.add(ytv);
        }
  
        return youtubeVideosList;
  
    }

	public Hashtable<String, ArrayList<YouTubeVideo>> getSearchResults() {
		return searchResults;
	}

	public void SelectVideosLessThan(int duration) {
		Enumeration<ArrayList<YouTubeVideo>> enumeration = searchResults.elements();
		ArrayList<YouTubeVideo> vids;
		int len;
		
		while (enumeration.hasMoreElements()) {
			vids = enumeration.nextElement();
			len = vids.size();
			
			for(int i = len-1; i>=0; i--){
				if(vids.get(i).getDuration() > duration*60){ // Checks if video length is more than specified time
					vids.remove(i);
				}
			}
		}
	}

	public void SelectVideosMoreThan(int duration) {
		Enumeration<ArrayList<YouTubeVideo>> enumeration = searchResults.elements();
		ArrayList<YouTubeVideo> vids;
		int len;
		
		while (enumeration.hasMoreElements()) {
			vids = enumeration.nextElement();
			len = vids.size();
			
			for(int i = len-1; i>=0; i--){
				if(vids.get(i).getDuration() < duration*60){ // Checks if video length is more than specified time
					vids.remove(i);
				}
			}
		}
	}

	public void selectVideosContainingKeywords(List<String> relatedKeywords) {
		Enumeration<ArrayList<YouTubeVideo>> enumeration = searchResults.elements();
		ArrayList<YouTubeVideo> vids;
		YouTubeVideo vid;
		boolean keywordFound;
		int len;
		
		while (enumeration.hasMoreElements()) {
			vids = enumeration.nextElement();
			len = vids.size();
			
			for(int i = len-1; i>=0; i--){
				vid = vids.get(i);
				keywordFound=false;
				
				for(String key: relatedKeywords){
					if(vid.getDescription().toLowerCase().contains(key.toLowerCase()) || vid.getTitle().toLowerCase().contains(key.toLowerCase())){
						keywordFound=true;
						break;
					}
				}
				
				if(keywordFound==false){
					vids.remove(i);
				}
			}
		}
	}

}
