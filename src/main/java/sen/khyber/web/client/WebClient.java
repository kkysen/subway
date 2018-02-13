package sen.khyber.web.client;

import sen.khyber.io.IO;

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
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    
    public WebClient(final @NotNull OkHttpClient client, final @NotNull Charset defaultCharset) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(defaultCharset);
        this.client = client;
        this.defaultCharset = defaultCharset;
    }
    
    public static final WebClient get() {
        return DEFAULT_WEB_CLIENT;
    }
    
    private final Map<String, WebResponse> responseCache = new HashMap<>();
    
    public WebResponse forUrl(final String url) {
        //noinspection resource
        return responseCache.computeIfAbsent(url, WebResponseImpl::new).refresh();
    }
    
    /**
     * Created by Khyber Sen on 1/29/2018.
     *
     * @author Khyber Sen
     */
    private final class WebResponseImpl implements WebResponse {
        
        private final String url;
        
        private @Nullable Response response;
        private @Nullable MediaType contentType;
        private @Nullable Charset charset;
        private @Nullable ByteBuffer byteBuffer;
        private @Nullable CharBuffer charBuffer;
        private @Nullable String string;
        private @Nullable Document document;
        private @Nullable HtmlPage renderedPage;
        private @Nullable Document renderedDocument;
        
        public WebResponseImpl(final @NotNull String url) {
            Objects.requireNonNull(url);
            this.url = url;
        }
        
        @Override
        public final @NotNull Response response() throws IOException {
            if (response == null) {
                response = client.newCall(new Request.Builder().url(url).build()).execute();
            }
            return response;
        }
        
        @Override
        public final @NotNull String realUrl() throws IOException {
            return response().request().url().toString();
        }
        
        @Override
        public final @NotNull ResponseBody body() throws IOException {
            return response().body();
        }
        
        @Override
        public final @NotNull MediaType contentType() throws IOException {
            if (contentType == null) {
                contentType = body().contentType();
            }
            return contentType;
        }
        
        @Override
        public final @NotNull Charset charset() throws IOException {
            if (charset == null) {
                charset = contentType().charset(defaultCharset);
            }
            return charset;
        }
        
        @Override
        public final @NotNull ByteBuffer byteBuffer() throws IOException {
            if (byteBuffer == null) {
                byteBuffer = ByteBuffer.wrap(body().bytes());
            }
            return byteBuffer;
        }
        
        @Override
        public final @NotNull CharBuffer charBuffer() throws IOException {
            if (charBuffer == null) {
                charBuffer = charset().decode(byteBuffer());
            }
            return charBuffer;
        }
        
        @Override
        public final long length() throws IOException {
            if (byteBuffer != null) {
                return byteBuffer.array().length;
            }
            return body().contentLength();
        }
        
        @Override
        public final @NotNull byte[] bytes() throws IOException {
            return byteBuffer().array();
        }
        
        @Override
        public final @NotNull char[] chars() throws IOException {
            return charBuffer().array();
        }
        
        @Override
        public final @NotNull String string() throws IOException {
            if (string == null) {
                if (charset() == StandardCharsets.ISO_8859_1) {
                    // TODO unsafe
                }
                string = charBuffer().toString();
            }
            return string;
        }
        
        @Override
        public final @NotNull StringBuilder append(final @NotNull StringBuilder sb)
                throws IOException {
            return sb.append(chars());
        }
        
        @Override
        public final @NotNull StringBuilder stringBuilder() throws IOException {
            return append(new StringBuilder(chars().length));
        }
        
        @Override
        public final @NotNull InputStream inputStream() throws IOException {
            return body().byteStream();
        }
        
        @Override
        public final @NotNull Reader reader() throws IOException {
            return body().charStream();
        }
        
        @Override
        public final @NotNull ByteBuffer put(final @NotNull ByteBuffer out) throws IOException {
            return out.put(byteBuffer());
        }
        
        @Override
        public final void download(final @NotNull Path path) throws IOException {
            put(IO.mmap(path));
        }
        
        @Override
        public final @NotNull Document document() throws IOException {
            if (document == null) {
                document = Jsoup.parse(inputStream(), charset().name(), realUrl());
            }
            return document;
        }
        
        @Override
        public final @NotNull HtmlPage renderedPage() throws IOException {
            if (renderedPage == null) {
                renderedPage = SilencedRenderingWebClient.get().getPage(realUrl());
            }
            //noinspection ConstantConditions
            return renderedPage;
        }
        
        @Override
        public final @NotNull Document renderedDocument() throws IOException {
            if (renderedDocument == null) {
                renderedDocument = Jsoup.parse(renderedPage().asXml());
            }
            return renderedDocument;
        }
        
        @Override
        public final void close() {
            if (response != null) {
                response.close();
            }
        }
        
        @Override
        public final @NotNull WebResponse refresh() {
            close();
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