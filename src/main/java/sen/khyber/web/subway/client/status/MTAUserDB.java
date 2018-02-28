package sen.khyber.web.subway.client.status;

import sen.khyber.util.ObjectUtils;
import sen.khyber.util.exceptions.ExceptionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Khyber Sen on 2/25/2018.
 *
 * @author Khyber Sen
 */
public abstract class MTAUserDB {
    
    private final int numCached;
    
    private final BlockingQueue<MTAUserUpdate[][]> userUpdatesQueue;
    
    protected MTAUserDB(final int numCached) {
        this.numCached = numCached;
        userUpdatesQueue = new ArrayBlockingQueue<>(numCached, true);
        CompletableFuture.runAsync(() -> {
            while (true) {
                final MTAUserUpdate[][] userUpdates = null; // TODO
                try {
                    userUpdatesQueue.put(userUpdates);
                } catch (final InterruptedException e) {
                    throw ExceptionUtils.atRuntime(e);
                }
            }
        });
    }
    
    public final @NotNull CompletableFuture<MTAUserUpdate[][]> fetchNewUsers(
            final @NotNull MTAUser[][][] currentUsers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userUpdatesQueue.take();
            } catch (final InterruptedException e) {
                throw ExceptionUtils.atRuntime(e);
            }
        });
    }
    
    protected abstract void fetchNewUsersDirectly(final @NotNull MTAUser[][][] currentUsers,
            long second, final @NotNull MTAUserUpdate[][] userUpdates);
    
    public final @NotNull MTAUserUpdate[][] fetchNewUsers(
            final @NotNull MTAUser[][][] currentUsers,
            final @NotNull Instant instant) {
        ObjectUtils.requireNonNull(currentUsers, instant);
        final Instant minute = instant.truncatedTo(ChronoUnit.MINUTES);
        final long second = minute.getEpochSecond();
        
        final MTAUserUpdate[][] userUpdates = new MTAUserUpdate[currentUsers.length][];
        for (int i = 0; i < currentUsers.length; i++) {
            userUpdates[i] = new MTAUserUpdate[currentUsers[i].length];
        }
        fetchNewUsersDirectly(currentUsers, second, userUpdates);
        return userUpdates;
        // TODO
        /*
        cache users/indices in mmap'ed buffers
        only fetch from DB when absolutely necessary
         */
    }
    
}