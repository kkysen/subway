package sen.khyber.web.subway.client;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
public class Feeds {
    
    private final String apiKey;
    private final int[] feedNumbers;
    
    public Feeds(final String apiKey, final int[] feedNumbers) {
        this.apiKey = apiKey;
        this.feedNumbers = feedNumbers;
    }
    
}