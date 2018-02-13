package sen.khyber.web.client;

import sen.khyber.unsafe.reflect.Reflector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.InteractivePage;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class SilencedRenderingWebClient extends WebClient {
    
    private static final long serialVersionUID = 1L;
    
    private static final BrowserVersion DEFAULT_BROWSER_VERSION = BrowserVersion.CHROME;
    
    static {
        //        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
        //                "org.apache.commons.logging.impl.NoOpLog");
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    }
    
    @SuppressWarnings("resource")
    private static final SilencedRenderingWebClient INSTANCE = new SilencedRenderingWebClient();
    
    public static final SilencedRenderingWebClient get() {
        return INSTANCE;
    }
    
    @Override
    public final void close() {
        super.close();
        //noinspection ConstantConditions
        Reflector.get().forClass(getClass()).field("INSTANCE").setToNull();
    }
    
    public SilencedRenderingWebClient(final BrowserVersion browserVersion) {
        super(browserVersion);
        
        final WebClientOptions options = getOptions();
        options.setCssEnabled(false);
        options.setThrowExceptionOnFailingStatusCode(false);
        options.setThrowExceptionOnScriptError(false);
        
        setCssErrorHandler(new SilentCssErrorHandler());
        
        setIncorrectnessListener((message, origin) -> {});
        
        setJavaScriptErrorListener(new SilencedJavaScriptErrorListener());
        
        setHTMLParserListener(new SilencedHTMLParserListener());
    }
    
    public SilencedRenderingWebClient() {
        this(DEFAULT_BROWSER_VERSION);
    }
    
    private static final class SilencedJavaScriptErrorListener implements JavaScriptErrorListener {
        
        @Override
        public void scriptException(final InteractivePage page,
                final ScriptException scriptException) {}
        
        @Override
        public void timeoutError(final InteractivePage page, final long allowedTime,
                final long executionTime) {}
        
        @Override
        public void malformedScriptURL(final InteractivePage page, final String url,
                final MalformedURLException malformedURLException) {}
        
        @Override
        public void loadScriptError(final InteractivePage page, final URL scriptUrl,
                final Exception exception) {}
        
    }
    
    private static final class SilencedHTMLParserListener implements HTMLParserListener {
        
        @Override
        public void warning(final String message, final URL url, final String html,
                final int line, final int column, final String key) {}
        
        @Override
        public void error(final String message, final URL url, final String html,
                final int line, final int column, final String key) {}
        
    }
    
}