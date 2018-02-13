package sen.khyber.web.subway.client;

import sen.khyber.util.Retrier;
import sen.khyber.util.Retrier.RetrierBuilders;
import sen.khyber.web.client.WebClient;
import sen.khyber.web.client.WebClient.WebResponseImpl;
import sen.khyber.web.subway.client.proto.FeedHeader;
import sen.khyber.web.subway.client.proto.FeedMessage;
import sen.khyber.web.subway.client.proto.NyctFeedHeader;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.ExtensionRegistry;

/**
 * Created by Khyber Sen on 1/31/2018.
 *
 * @author Khyber Sen
 */
public class SubwaySystem {
    
    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();
    
    static {
        for (final Descriptor descriptor : new Descriptor[] {
                NyctFeedHeader.getDescriptor(),
        }) {
            descriptor.getFields().forEach(registry::add);
        }
    }
    
    private final WebClient client = WebClient.get();
    
    private final String apiKey;
    
    private final int intervalSeconds = 30;
    private final int sleepLengthMillis = 1000;
    
    public SubwaySystem(final String apiKey) {
        this.apiKey = apiKey;
    }
    
    private void update(final FeedMessage message) {
        final FeedHeader header = message.getHeader();
        //        NyctFeedHeader.getDescriptor().getFields()
    }
    
    private void update(final WebResponseImpl update) throws IOException {
        update(FeedMessage.parseFrom(update.byteBuffer(), registry));
    }
    
    private void update(final Feed feed) throws IOException {
        update(client.forUrl(feed.url(apiKey)));
    }
    
    private static final RetrierBuilders<Feed> feedRetrierBuilders = Retrier.builders();
    
    public List<Pair<Feed, IOException>> update() {
        // two-thirds of safe interval
        final int safeInterval = intervalSeconds * (int) 1e6 * 2 / 3;
        final long start = System.nanoTime();
        return feedRetrierBuilders
                .exceptionalIO((unfinished, failed) -> {
                    unfinished.parallelStream()
                            .forEach(feed -> {
                                try {
                                    update(feed);
                                } catch (IOException e) {
                                    failed.add(Pair.of(feed, e));
                                }
                            });
                })
                .sleepLength(sleepLengthMillis)
                .stopTrying((a, b) -> System.nanoTime() - start >= safeInterval)
                .log(null)
                .build()
                .keepTrying(Feed.values());
    }
    
}