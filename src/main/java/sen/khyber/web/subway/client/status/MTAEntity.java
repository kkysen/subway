package sen.khyber.web.subway.client.status;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
public interface MTAEntity {
    
    public int ordinal();
    
    public @NotNull String officialName();
    
    public default @NotNull String fileName() {
        return ordinal() + ". " + officialName();
    }
    
}