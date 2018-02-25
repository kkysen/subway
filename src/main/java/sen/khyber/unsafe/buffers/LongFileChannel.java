package sen.khyber.unsafe.buffers;

import sen.khyber.unsafe.UnsafeUtils;
import sen.khyber.unsafe.reflect.ReflectedClass;
import sen.khyber.unsafe.reflect.ReflectedField;
import sen.khyber.unsafe.reflect.Reflectors;
import sen.khyber.util.exceptions.ExceptionUtils;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Khyber Sen
 */
public class LongFileChannel extends FileChannel {
    
    private static @NotNull ReflectedClass<?> sunNioCh(final @NotNull String className) {
        return Reflectors.main().get("sun.nio.ch." + className).orElseThrow(AssertionError::new);
    }
    
    private static final @NotNull ReflectedClass<?> FileChannelImplClass =
            sunNioCh("FileChannelImpl");
    
    private static final @NotNull ReflectedClass<?> NativeThreadSetClass =
            sunNioCh("NativeThreadSet");
    
    private static final @NotNull ReflectedClass<?> FileDispatcherImplClass =
            sunNioCh("FileDispatcherImpl");
    
    private static @NotNull ReflectedField field(final @NotNull ReflectedClass<?> klass,
            final @NotNull String name) {
        return klass.fieldUnchecked(name);
    }
    
    private static @NotNull MethodHandle methodHandle(final @NotNull ReflectedClass<?> klass,
            final @NotNull String name, final @NotNull Class<?>... parameterTypes) {
        return klass.methodUnchecked(name, parameterTypes).handle();
    }
    
    private static final @NotNull MethodHandle openStaticMethod =
            methodHandle(FileChannelImplClass, "open",
                    FileDescriptor.class, String.class, boolean.class, boolean.class, Object.class);
    
    private static final @NotNull MethodHandle implCloseChannelMethod =
            methodHandle(FileChannelImplClass, "implCloseChannel");
    
    private static final long allocationGranularity =
            field(FileChannelImplClass, "allocationGranularity").getLong();
    
    private static final int MAP_RW = 1;
    
    // from sun.nio.ch.IOStatus
    private static final int EOF = -1;
    private static final int INTERRUPTED = -3;
    private static final int UNSUPPORTED_CASE = -6;
    
    private static final @NotNull ReflectedField threadsField =
            field(FileChannelImplClass, "threads");
    private static final @NotNull ReflectedField ndField = field(FileChannelImplClass, "nd");
    
    private static final @NotNull MethodHandle map0Method =
            methodHandle(FileChannelImplClass, "map0", int.class, long.class, long.class);
    private static final @NotNull MethodHandle unmap0Method =
            methodHandle(FileChannelImplClass, "unmap0", long.class, long.class);
    
    private static final @NotNull MethodHandle threadsAddMethod =
            methodHandle(NativeThreadSetClass, "add");
    private static final @NotNull MethodHandle threadsRemoveMethod =
            methodHandle(NativeThreadSetClass, "remove", int.class);
    
    private static final @NotNull MethodHandle ndDuplicateForMappingMethod =
            methodHandle(FileDispatcherImplClass, "duplicateForMapping", FileDescriptor.class);
    private static final @NotNull MethodHandle ndTruncateMethod =
            methodHandle(FileDispatcherImplClass, "truncate", FileDescriptor.class, long.class);
    private static final @NotNull MethodHandle ndCloseMethod =
            methodHandle(FileDispatcherImplClass, "close", FileDescriptor.class);
    
    private @NotNull ReflectedField bind(final @NotNull ReflectedField field) {
        return field.clone().bind(impl);
    }
    
    private final FileChannel impl;
    private final FileDescriptor fd;
    
    private final ReflectedField threads;
    private final ReflectedField nd;
    
    public LongFileChannel(final FileDescriptor fd, final String path, final boolean readable,
            final boolean writable, final Object parent) {
        try {
            impl = (FileChannel) openStaticMethod
                    .invokeWithArguments(fd, path, readable, writable, parent);
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
        this.fd = fd;
        threads = bind(threadsField);
        nd = bind(ndField);
    }
    
    public LongFileChannel(final RandomAccessFile raf) throws IOException {
        impl = raf.getChannel();
        fd = raf.getFD();
        threads = bind(threadsField);
        nd = bind(ndField);
    }
    
    @Override
    public final long size() throws IOException {
        return impl.size();
    }
    
    @Override
    public final FileChannel truncate(final long size) throws IOException {
        return impl.truncate(size);
    }
    
    @Override
    public final long position() throws IOException {
        return impl.position();
    }
    
    @Override
    public final FileChannel position(final long newPosition) throws IOException {
        return impl.position(newPosition);
    }
    
    @Override
    public final void force(final boolean metaData) throws IOException {
        impl.force(metaData);
    }
    
    @Override
    public final int read(final ByteBuffer dst) throws IOException {
        return impl.read(dst);
    }
    
    @Override
    public final int read(final ByteBuffer dst, final long position) throws IOException {
        return impl.read(dst, position);
    }
    
    @Override
    public final long read(final ByteBuffer[] dsts, final int offset, final int length)
            throws IOException {
        return impl.read(dsts, offset, length);
    }
    
    @Override
    public final int write(final ByteBuffer src) throws IOException {
        return impl.write(src);
    }
    
    @Override
    public final int write(final ByteBuffer src, final long position) throws IOException {
        return impl.write(src, position);
    }
    
    @Override
    public final long write(final ByteBuffer[] srcs, final int offset, final int length)
            throws IOException {
        return impl.write(srcs, offset, length);
    }
    
    @Override
    public final long transferTo(final long position, final long count,
            final WritableByteChannel target)
            throws IOException {
        return impl.transferTo(position, count, target);
    }
    
    @Override
    public final long transferFrom(final ReadableByteChannel src, final long position,
            final long count)
            throws IOException {
        return impl.transferFrom(src, position, count);
    }
    
    @Override
    public final MappedByteBuffer map(final MapMode mode, final long position, final long size)
            throws IOException {
        return impl.map(mode, position, size);
    }
    
    @Override
    public final FileLock lock(final long position, final long size, final boolean shared)
            throws IOException {
        return impl.lock(position, size, shared);
    }
    
    @Override
    public final FileLock tryLock(final long position, final long size, final boolean shared)
            throws IOException {
        return impl.tryLock(position, size, shared);
    }
    
    @Override
    protected final void implCloseChannel() throws IOException {
        try {
            implCloseChannelMethod.invoke(impl);
        } catch (final Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
    /*
     * TODO
     * possible to rewrite simple NativeThreadSet class
     * and just have the NativeThread.current() method
     */
    
    private Object threads() {
        return threads.getObject();
    }
    
    private int threadsAdd() {
        try {
            return (int) threadsAddMethod.invoke(threads());
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private void threadsRemove(final int threadId) {
        try {
            threadsRemoveMethod.invoke(threads(), threadId);
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    private Object nd() {
        return nd.getObject();
    }
    
    private FileDescriptor ndDuplicateForMapping(final FileDescriptor fd) throws IOException {
        try {
            return (FileDescriptor) ndDuplicateForMappingMethod.invoke(nd(), fd);
        } catch (final Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
    private int ndTruncate(final FileDescriptor fd, final long size) throws IOException {
        try {
            return (int) ndTruncateMethod.invoke(nd(), fd, size);
        } catch (final Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
    private static boolean IOStatuscheckAll(final long n) {
        return n > EOF || n < UNSUPPORTED_CASE;
    }
    
    public void ensureOpen() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }
    
    private long map0(final int iMode, final long position, final long size)
            throws IOException {
        try {
            return (long) map0Method.invoke(impl, iMode, position, size);
        } catch (final Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw ExceptionUtils.atRuntime(e);
            }
        }
    }
    
    private static int unmap0(final long address, final long size) {
        try {
            return (int) unmap0Method.invokeExact(address, size);
        } catch (final Throwable e) {
            throw ExceptionUtils.atRuntime(e);
        }
    }
    
    public final @Nullable UnsafeMappedBuffer longRWMap(final long position, final long size)
            throws IOException {
        if (size == 0) {
            System.err.println("Warning: size is 0 (" + getClass() + ')');
            return null; // FIXME is this right, or should it be buffer w/ 0 length
        }
        
        ensureOpen();
        assert position >= 0;
        assert size >= 0;
        final int iMode = MAP_RW;
        
        long address = -1;
        int threadId = -1;
        
        try {
            begin();
            threadId = threadsAdd();
            if (!isOpen()) {
                return null;
            }
            if (size() < position + size) {
                int rv;
                do {
                    rv = ndTruncate(fd, position + size);
                } while (rv == INTERRUPTED && isOpen());
            }
            
            final int pagePosition = (int) (position % allocationGranularity);
            final long mapPosition = position - pagePosition;
            final long mapSize = size + pagePosition;
            try {
                address = map0(iMode, mapPosition, mapSize);
            } catch (final OutOfMemoryError e1) {
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
                try {
                    address = map0(iMode, mapPosition, mapSize);
                } catch (final OutOfMemoryError e3) {
                    throw new IOException("mmap failed, out of memory", e3);
                }
            }
            
            final FileDescriptor mappedFd;
            try {
                mappedFd = ndDuplicateForMapping(fd);
            } catch (final IOException e) {
                unmap0(address, mapSize);
                throw e;
            }
            
            assert IOStatuscheckAll(address);
            assert address % allocationGranularity == 0;
            final Unmapper unmapperImpl = new UnmapperImpl(address, mapSize,
                    mappedFd);
            return new UnsafeMappedBuffer(address + pagePosition, size, unmapperImpl);
        } finally {
            threadsRemove(threadId);
            end(IOStatuscheckAll(address));
        }
    }
    
    /**
     * @author Khyber Sen
     */
    @FunctionalInterface
    static interface Unmapper {
        
        public void unmap();
        
    }
    
    /**
     * @author Khyber Sen
     */
    @RequiredArgsConstructor
    @ToString
    private static final class UnmapperImpl implements Unmapper {
        
        private static final Object nd = FileDispatcherImplClass.allocateInstance();
        
        private final long address;
        private final long size;
        private final FileDescriptor fd;
        
        private static void ndClose(final FileDescriptor fd) throws IOException {
            try {
                ndCloseMethod.invoke(nd, fd);
            } catch (final Throwable e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                } else {
                    throw ExceptionUtils.atRuntime(e);
                }
            }
        }
        
        @Override
        public final void unmap() {
            if (address == UnsafeUtils.NULLPTR) {
                return;
            }
            unmap0(address, size);
            
            if (fd.valid()) {
                try {
                    ndClose(fd);
                } catch (final IOException e) {}
            }
        }
        
    }
    
}