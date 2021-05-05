package com.youtube.channel.config;

public class EndPoints {
    
    public static String TAG = EndPoints.class.getSimpleName();
    public static final String API_KEY = "AIzaSyDH3naOGPlOL175VfhVaRrzr0438MymNxM";
    public static final String POPULAR_VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&chart=mostPopular&maxResults=15&key=" + API_KEY;
    public static final String SEARCH_VIDEO_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=15&key=" + API_KEY;
    public static final String VIDEO_DETAILS_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&key=" + API_KEY;
    public static final String YOUTUBE_URL_VIDEO = "http://www.youtube.com/watch?v=";
    public static final String YOUTUBE_CHANNEL_ID = "UC2H7DyQrnr2RA4RSMF0B4ZA";
    public static final String YOUTUBE_THUMBNAIL = "https://i.ytimg.com/vi/"; 
    public static final String THUMBNAIL_HIGH = "/hqdefault.jpg";
}
