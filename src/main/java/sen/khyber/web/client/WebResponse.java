package sen.khyber.web.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Khyber Sen on 2/13/2018.
 *
 * @author Khyber Sen
 */
public interface WebResponse extends Closeable {
    
    public @NotNull Response response() throws IOException;
    
    public @NotNull String realUrl() throws IOException;
    
    public @NotNull ResponseBody body() throws IOException;
    
    public @NotNull MediaType contentType() throws IOException;
    
    public @NotNull Charset charset() throws IOException;
    
    public @NotNull ByteBuffer byteBuffer() throws IOException;
    
    public @NotNull CharBuffer charBuffer() throws IOException;
    
    public long length() throws IOException;
    
    public @NotNull byte[] bytes() throws IOException;
    
    public @NotNull char[] chars() throws IOException;
    
    public @NotNull String string() throws IOException;
    
    public @NotNull StringBuilder append(final @NotNull StringBuilder sb) throws IOException;
    
    public @NotNull StringBuilder stringBuilder() throws IOException;
    
    public @NotNull InputStream inputStream() throws IOException;
    
    public @NotNull Reader reader() throws IOException;
    
    public @NotNull ByteBuffer put(final @NotNull ByteBuffer out) throws IOException;
    
    public void download(final @NotNull Path path) throws IOException;
    
    public @NotNull Document document() throws IOException;
    
    public @NotNull HtmlPage renderedPage() throws IOException;
    
    public @NotNull Document renderedDocument() throws IOException;
    
    public @NotNull WebResponse refresh();
    
}