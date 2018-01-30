package sen.khyber.web.client;

import sen.khyber.io.IO;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class WebClient {
    
    private static final OkHttpClient DEFAULT_CLIENT = new OkHttpClient();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    private static final WebClient DEFAULT_WEB_CLIENT =
            new WebClient(DEFAULT_CLIENT, DEFAULT_CHARSET);
    
    private final OkHttpClient client;
    private final Charset defaultCharset;
    
    public WebClient(final @NonNull OkHttpClient client, final @NonNull Charset defaultCharset) {
        this.client = client;
        this.defaultCharset = defaultCharset;
    }
    
    public static final WebClient get() {
        return DEFAULT_WEB_CLIENT;
    }
    
    private final Map<String, WebResponse> responseCache = new HashMap<>();
    
    public WebResponse forUrl(final String url) {
        return responseCache.computeIfAbsent(url, WebResponse::new).refresh();
    }
    
    /**
     * Created by Khyber Sen on 1/29/2018.
     *
     * @author Khyber Sen
     */
    public class WebResponse {
        
        private final String url;
        
        private Response response;
        private MediaType contentType;
        private Charset charset;
        private ByteBuffer byteBuffer;
        private CharBuffer charBuffer;
        private String string;
        private Document document;
        private HtmlPage renderedPage;
        private Document renderedDocument;
        
        public WebResponse(final @NonNull String url) {
            this.url = url;
        }
        
        public Response response() throws IOException {
            if (response == null) {
                response = client.newCall(new Request.Builder().url(url).build()).execute();
            }
            return response;
        }
        
        public String realUrl() throws IOException {
            return response().request().url().toString();
        }
        
        public ResponseBody body() throws IOException {
            return response().body();
        }
        
        public MediaType contentType() throws IOException {
            if (contentType == null) {
                contentType = body().contentType();
            }
            return contentType;
        }
        
        public Charset charset() throws IOException {
            if (charset == null) {
                charset = contentType().charset(defaultCharset);
            }
            return charset;
        }
        
        public ByteBuffer byteBuffer() throws IOException {
            if (byteBuffer == null) {
                byteBuffer = ByteBuffer.wrap(body().bytes());
            }
            return byteBuffer;
        }
        
        public CharBuffer charBuffer() throws IOException {
            if (charBuffer == null) {
                charBuffer = charset().decode(byteBuffer());
            }
            return charBuffer;
        }
        
        public long length() throws IOException {
            if (byteBuffer != null) {
                return byteBuffer.array().length;
            }
            return body().contentLength();
        }
        
        public byte[] bytes() throws IOException {
            return byteBuffer().array();
        }
        
        public char[] chars() throws IOException {
            return charBuffer().array();
        }
        
        public String string() throws IOException {
            if (string == null) {
                if (charset() == StandardCharsets.ISO_8859_1) {
                    // TODO unsafe
                }
                string = charBuffer.toString();
            }
            return string;
        }
        
        public StringBuilder append(final StringBuilder sb) throws IOException {
            return sb.append(chars());
        }
        
        public StringBuilder stringBuilder() throws IOException {
            return append(new StringBuilder(chars().length));
        }
        
        public InputStream inputStream() throws IOException {
            return body().byteStream();
        }
        
        public Reader reader() throws IOException {
            return body().charStream();
        }
        
        public ByteBuffer put(final ByteBuffer out) throws IOException {
            return out.put(byteBuffer());
        }
        
        public void download(final Path path) throws IOException {
            put(IO.mmap(path));
        }
        
        public Document document() throws IOException {
            if (document == null) {
                document = Jsoup.parse(inputStream(), charset().name(), realUrl());
            }
            return document;
        }
        
        public HtmlPage renderedPage() throws IOException {
            if (renderedPage == null) {
                renderedPage = SilencedRenderingWebClient.get().getPage(realUrl());
            }
            return renderedPage;
        }
        
        public Document renderedDocument() throws IOException {
            if (renderedDocument == null) {
                renderedDocument = Jsoup.parse(renderedPage().asXml());
            }
            return renderedDocument;
        }
        
        public WebResponse refresh() {
            response = null;
            contentType = null;
            charset = null;
            byteBuffer = null;
            charBuffer = null;
            string = null;
            document = null;
            renderedPage = null;
            renderedDocument = null;
            
            return this;
        }
        
    }
    
}