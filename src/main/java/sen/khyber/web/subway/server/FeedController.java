package sen.khyber.web.subway.server;

import sen.khyber.web.client.WebClient;
import sen.khyber.web.subway.client.proto.FeedMessage;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.googlecode.protobuf.format.JsonFormat;

/**
 * Created by Khyber Sen on 1/30/2018.
 *
 * @author Khyber Sen
 */
@RestController
public class FeedController {
    
    @RequestMapping("/subway")
    public String feed() throws IOException {
        final String url = "http://datamine.mta.info/mta_esi.php"
                + "?feed_id=1"
                + "&key=d7eb784b9ca027e13f9fed6dbe48e2b8";
        final byte[] bytes = WebClient.get().forUrl(url).bytes();
        final FeedMessage feed = FeedMessage.parseFrom(bytes);
        //        final FeedEntity entity = feed.getEntity(0);
        //        System.out.println(entity);
        feed.getEntity(0).getTripUpdate().getStopTimeUpdate(0).getArrival().getTime();
        final String json = new JsonFormat().printToString(feed);
        return "<script>window.feed = " + json + ";</script>";
    }
    
}