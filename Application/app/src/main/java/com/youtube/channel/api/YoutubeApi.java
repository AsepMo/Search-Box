package com.youtube.channel.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeApi {
    
    public static String TAG = YoutubeApi.class.getSimpleName();
    
    /**
     * Please replace this with a valid API key which is enabled for the 
     * YouTube Data API v3 service. Go to the 
     * <a href="https://code.google.com/apis/console/">Google APIs Console</a> to
     * register a new developer key.
     */
    // This is the value of Intent.EXTRA_LOCAL_ONLY for API level 11 and above.
    public static final String DEVELOPER_KEY = "AIzaSyAYgHbHDXV1x-wSdJPqdPiwq-2GgdWEqWk";
    public static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
    
    public static final String YOUTUBE_WEB_URL = "https://www.youtube.com/";
    public static final String YOUTUBE_ASEPMO_USER_ID = "maulana8608";
    public static final String YOUTUBE_ASEPMO_CHANNEL_URL = "https://m.youtube.com/channel/UC2H7DyQrnr2RA4RSMF0B4ZA";
    public static final String YOUTUBE_ASEPMO_VIDEO_ = "https://www.youtube.com/watch?v=QrAsNq5h7hU";
    public static final String YOUTUBE_ASEPMO_VIDEO_ID = "QrAsNq5h7hU";
    public static final String YOUTUBE_BERANDA = "PLiKkX4KV1eFLUxsoE7fIDx5RDSC0qOdC4";
    public static final String YOUTUBE_SEARCH_WEB_PAGE = "https://www.youtube.com/results?search_query=";
    /* Menu Playlist */
    public static final String YOUTUBE_TUTORIAL_ = "Lagi Dirilis";
    
    public static final String YOUTUBE_MOVIE_HORROR = "PLiKkX4KV1eFKBUbzyeaZzHA-u3mm25Imb";
    public static final String YOUTUBE_MOVIE_ANIMATION = "PLiKkX4KV1eFKTi5Ypj50U2YVyOh3nCih1";
    public static final String YOUTUBE_MOVIE_COMEDY = "PLiKkX4KV1eFJGLDJEr0hnhN7HANcM_zxs";
    //Youtube Music
    public static final String YOUTUBE_MUSIC_ = "PLiKkX4KV1eFIEF7n3_J6wjk2pOip2D73i";
    //Youtube Tic Toc
    public static final String YOUTUBE_TICTOC = "PLiKkX4KV1eFL3yWTGci_rE-6adI_YwLyz";
    public static final String YOUTUBE_STREET_FASHION = "PLiKkX4KV1eFKrTzX0UryQVkZ8pArFQ-BF";
    public static final String YOUTUBE_GIRLBAND_TARA = "PLiKkX4KV1eFJpdErP6Lp6Wje_Xk0ZZBRY";
    
    public static final String SCTV = "PLiKkX4KV1eFIsqwx3r2ICms0sWIj8caS4";
    
    //public static final String YoutubeVideoUrl = YoutubeInfo.getVideoPlay();
    public static final String PROFILE_PAGE_URL = "https://aweb41.github.io/AWeb/";
    public static final String ASEPMO_PAGE_URL = "https://asepmo.github.io/AsepMo/";
    public static final String MESSAGE_PAGE_URL = "https://aweb41.github.io/AWeb/";
    public static final String XTERMINAL_PAGE_URL = "https://asepmo.github.io/AsepMo/";
    public static final String AWEB_PAGE_URL = "https://aweb41.github.io/AWeb/";
	public static final String Y2MATE_PAGE_URL = "https://www.y2mate.com/youtube/";
    
    public static String getVideoIdFromYoutubeUrl(String url) {
        Matcher matcher = Pattern.compile("http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)", 2).matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
