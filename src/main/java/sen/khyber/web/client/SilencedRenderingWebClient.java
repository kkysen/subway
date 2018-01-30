package sen.khyber.web.client;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.InteractivePage;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class SilencedRenderingWebClient extends WebClient {
    
    private static final long serialVersionUID = 1L;
    
    private static final BrowserVersion DEFAULT_BROWSER_VERSION = BrowserVersion.CHROME;
    
    static {
        //        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
        //                "org.apache.commons.logging.impl.NoOpLog");
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    }
    
    private static final SilencedRenderingWebClient INSTANCE = new SilencedRenderingWebClient();
    
    public static SilencedRenderingWebClient get() {
        return INSTANCE;
    }
    
    public SilencedRenderingWebClient(final BrowserVersion browserVersion) {
        super(browserVersion);
        
        final WebClientOptions options = getOptions();
        options.setCssEnabled(false);
        options.setThrowExceptionOnFailingStatusCode(false);
        options.setThrowExceptionOnScriptError(false);
        
        setCssErrorHandler(new SilentCssErrorHandler());
        
        setIncorrectnessListener((message, origin) -> {});
        
        setJavaScriptErrorListener(new JavaScriptErrorListener() {
            
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
            
        });
        
        setHTMLParserListener(new HTMLParserListener() {
            
            @Override
            public void warning(final String message, final URL url, final String html,
                    final int line, final int column, final String key) {}
            
            @Override
            public void error(final String message, final URL url, final String html,
                    final int line, final int column, final String key) {}
        });
    }
    
    public SilencedRenderingWebClient() {
        this(DEFAULT_BROWSER_VERSION);
    }
    
}