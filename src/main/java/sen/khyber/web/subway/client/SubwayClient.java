package sen.khyber.web.subway.client;

import sen.khyber.web.client.WebClient;
import sen.khyber.web.subway.client.proto.FeedEntity;
import sen.khyber.web.subway.client.proto.FeedMessage;

import java.io.IOException;

import com.googlecode.protobuf.format.JsonFormat;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class SubwayClient {
    
    public static void main(final String[] args) throws IOException {
        final String url = "http://datamine.mta.info/mta_esi.php"
                + "?feed_id=1"
                + "&key=d7eb784b9ca027e13f9fed6dbe48e2b8";
        final byte[] bytes = WebClient.get().forUrl(url).bytes();
        final FeedMessage feed = FeedMessage.parseFrom(bytes);
        final FeedEntity entity = feed.getEntity(0);
        System.out.println(entity);
        new JsonFormat().printToString(feed);
    }
    
}