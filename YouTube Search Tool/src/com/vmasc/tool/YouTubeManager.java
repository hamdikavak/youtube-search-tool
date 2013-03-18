package com.vmasc.tool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.client.Query;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Person;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;


public class YouTubeManager {
 
    private static final String YOUTUBE_URL = "http://gdata.youtube.com/feeds/api/videos";
    private static final String YOUTUBE_EMBEDDED_URL = "http://www.youtube.com/v/";
 
    private String clientID;
	private ArrayList<String> msRelatedKeywords;
	
    public YouTubeManager(String clientID) {
        this.clientID = clientID;
        
        msRelatedKeywords = new ArrayList<String>();
        //System.out.println(this.getClass().getClassLoader().getResource(".").getPath());
		
        
        try {
        	
        	//String msFilePath = new java.io.File(".").getCanonicalPath()+"\\ms_related_keywords.txt";
        	
			  InputStream in = this.getClass().getClassLoader().getResourceAsStream("ms_related_keywords.txt");
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  
			  while ((strLine = br.readLine()) != null)   {
				  msRelatedKeywords.add(strLine.toLowerCase());
			  }
		
			  in.close();
			 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
    }
 
     public List<YouTubeVideo> retrieveVideos(String textQuery, int maxResults, boolean filter, int timeout, int startIndex) throws Exception {
  
        YouTubeService service = new YouTubeService(clientID);
        service.setConnectTimeout(timeout); // millis
        YouTubeQuery query = new YouTubeQuery(new URL(YOUTUBE_URL));
  
        query.setOrderBy(YouTubeQuery.OrderBy.RELEVANCE);
        query.setFullTextQuery(textQuery);
        query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
        query.setMaxResults(maxResults);
        query.setStartIndex(startIndex);
        query.addCustomParameter(new Query.CustomParameter("hd", "true"));

        VideoFeed videoFeed = service.query(query, VideoFeed.class);  
        List<VideoEntry> videos = videoFeed.getEntries();
  
        return convertVideos(videos);
  
    }
 
    private List<YouTubeVideo> convertVideos(List<VideoEntry> videos) {
  
        List<YouTubeVideo> youtubeVideosList = new LinkedList<YouTubeVideo>();
        int duration;
        boolean isRelated;
        for (VideoEntry videoEntry : videos) {
        	
        	YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
        	
        	try{
        	duration = mediaGroup.getYouTubeContents().get(0).getDuration();
        	}
        	catch(Exception e){
        		duration=0;
        	}
        	isRelated = true;//checkContext(videoEntry.getTitle().getPlainText(), mediaGroup.getDescription().getPlainTextContent());
        	if(duration <= 300 && isRelated==true){
	            YouTubeVideo ytv = new YouTubeVideo();
	            String personName="";
	            for(Person p: videoEntry.getAuthors()){
	            	personName+=p.getName();
	            	break;
	            }
	            
	            String webPlayerUrl = mediaGroup.getPlayer().getUrl();
	            ytv.setWebPlayerUrl(webPlayerUrl);
	   
	            ytv.setDuration( duration );
	            ytv.setDescription(mediaGroup.getDescription().getPlainTextContent());
	            ytv.setVideoOwner(personName);
	            
	            String query = "?v=";
	            int index = webPlayerUrl.indexOf(query);
	
	            String embeddedWebPlayerUrl = webPlayerUrl.substring(index+query.length());
	            embeddedWebPlayerUrl = YOUTUBE_EMBEDDED_URL + embeddedWebPlayerUrl;
	            ytv.setEmbeddedWebPlayerUrl(embeddedWebPlayerUrl);
	   
	            List<String> thumbnails = new LinkedList<String>();
	            for (MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
	                thumbnails.add(mediaThumbnail.getUrl());
	            }   
	            ytv.setThumbnails(thumbnails);
	            
	            List<YouTubeMedia> medias = new LinkedList<YouTubeMedia>();
	            for (YouTubeMediaContent mediaContent : mediaGroup.getYouTubeContents()) {
	                medias.add(new YouTubeMedia(mediaContent.getUrl(), mediaContent.getType()));
	            }
	            ytv.setMedias(medias);
	            ytv.setTitle(videoEntry.getTitle().getPlainText());
	            //ytv.setDescription(videoEntry.getTextContent().getContent().getPlainText());
	            
	            String[] idParts = videoEntry.getId().split(":");
	            
	            ytv.setVideoId(idParts[idParts.length-1]);
	            youtubeVideosList.add(ytv);
            
        	}
   
        }
  
        return youtubeVideosList;
  
    }

	private boolean checkContext(String title, String description){

		for(String key: msRelatedKeywords){
			if(title.toLowerCase().contains(key) || description.toLowerCase().contains(key) ){
				return true;
			}
		}
		
		return false;
	}
}
