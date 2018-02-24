package sen.khyber.unsafe.gc;

import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.experimental.ExtensionMethod;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.management.InstanceNotFoundException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import org.jetbrains.annotations.NotNull;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

/**
 * @author Khyber Sen
 */
@SuppressWarnings("restriction")
@ExtensionMethod({GarbageCollectionNotificationInfo.class, ExceptionUtils.class})
public final class PostGC {
    
    private PostGC() {}
    
    private static final @NotNull List<Consumer<? super GCEvent>> gcListeners = new ArrayList<>();
    
    public static void addGCListener(final @NotNull Consumer<? super GCEvent> gcListener) {
        Objects.requireNonNull(gcListener);
        gcListeners.add(gcListener);
    }
    
    public static void runAfterGC(final @NotNull Runnable action) {
        Objects.requireNonNull(action);
        addGCListener(gcEvent -> action.run());
    }
    
    private static final NotificationListener listener =
            (Notification notification, Object handback) -> {
                if (!notification.getType()
                        .equals(GarbageCollectionNotificationInfo
                                .GARBAGE_COLLECTION_NOTIFICATION)) {
                    return;
                }
                final CompositeData cd = (CompositeData) notification.getUserData();
                final GarbageCollectionNotificationInfo gcNotificationInfo =
                        GarbageCollectionNotificationInfo.from(cd);
                final GcInfo gcInfo = gcNotificationInfo.getGcInfo();
                final GCEvent gcEvent =
                        new GCEvent(notification, handback, cd, gcNotificationInfo, gcInfo);
                for (final Consumer<? super GCEvent> gcListener : gcListeners) {
                    gcListener.accept(gcEvent);
                }
            };
    
    private static void startListening() {
        for (final GarbageCollectorMXBean gcMbean : ManagementFactory
                .getGarbageCollectorMXBeans()) {
            try {
                ManagementFactory.getPlatformMBeanServer()
                        .addNotificationListener(gcMbean.getObjectName(), listener, null, null);
            } catch (final InstanceNotFoundException e) {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
    static {
        startListening();
    }
    
}
