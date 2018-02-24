package sen.khyber.unsafe.gc;

import javax.management.Notification;
import javax.management.openmbean.CompositeData;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 * 
 * @author Khyber Sen
 */
@SuppressWarnings("restriction")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true, chain = false)
@Getter
public class GCEvent {
    
    private final Notification notification;
    private final Object handback;
    private final CompositeData data;
    private final GarbageCollectionNotificationInfo gcNotificationInfo;
    private final GcInfo gcInfo;
    
}
