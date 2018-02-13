package sen.khyber.io;

import sen.khyber.unsafe.fields.ByteBufferUtils;
import sen.khyber.unsafe.fields.StringUtils;
import sen.khyber.util.exceptions.ExceptionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.apache.kafka.common.utils.ByteBufferInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public final class IO {
    
    private IO() {}
    
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    private static final int BUFFER_SIZE = 8092;
    
    public static final Path Home = Paths.get("C:/Users/kkyse");
    public static final Path Desktop = Home.resolve("Desktop");
    public static final Path Downloads = Home.resolve("Downloads");
    public static final Path Documents = Home.resolve("Documents");
    public static final Path OneDrive = Home.resolve("OneDrive");
    public static final Path Workspace = Home.resolve("workspace");
    
    public static final Path Project = Workspace.resolve("Subway");
    public static final Path ProjectMain = Project.resolve("src").resolve("main");
    public static final Path ProjectJava = ProjectMain
            .resolve("java")
            .resolve("sen")
            .resolve("khyber");
    public static final Path ProjectResources = ProjectMain
            .resolve("resources")
            .resolve("sen")
            .resolve("khyber");
    public static final Path ProjectWebResources = ProjectResources.resolve("web");
    public static final Path ProjectWebClientResources = ProjectWebResources.resolve("client");
    public static final Path ProjectWebServerResources = ProjectWebResources.resolve("server");
    
    public static final long B = 1;
    public static final long KB = B << 10;
    public static final long MB = KB << 10;
    public static final long GB = MB << 10;
    public static final long TB = GB << 10;
    public static final long PB = TB << 10;
    public static final long EB = PB << 10;
    
    public static final String getExtension(final Path path) {
        final String file = path.getFileName().toString();
        final int dotIndex = file.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return file.substring(dotIndex + 1);
    }
    
    public static final Path removeExtension(Path path) {
        final String file = path.getFileName().toString();
        final int dotIndex = file.lastIndexOf('.');
        path = path.getParent();
        if (dotIndex != -1) {
            path.resolve(file.substring(0, dotIndex));
        }
        return path;
    }
    
    public static final Path setExtension(Path path, final String extension) {
        String file = path.getFileName().toString();
        path = path.getParent();
        final int dotIndex = file.lastIndexOf('.');
        if (dotIndex == -1) {
            file += '.' + extension;
        } else {
            file = file.substring(0, dotIndex + 1) + extension;
        }
        return path.resolve(file);
    }
    
    public static final void save(final Object o, final Path path) throws IOException {
        Files.deleteIfExists(path);
        final ByteBuffer bytes = ByteBuffer.wrap(StringUtils.getByteArray(o.toString()));
        IO.mmap(path, bytes.limit()).put(bytes);
    }
    
    public static final void save(final Object o, final String path) throws IOException {
        IO.save(o, Paths.get(path));
    }
    
    public static final long copy(final InputStream in, final OutputStream out) throws IOException {
        long bytesRead = 0;
        final byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
            bytesRead += n;
        }
        return bytesRead;
    }
    
    public static final long copySlow(final InputStream in, final Path path) throws IOException {
        Files.deleteIfExists(path);
        try (final OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW)) {
            return copy(in, out);
        }
    }
    
    public static final long copy(final InputStream in, final Path path) throws IOException {
        final ReadableByteChannel rbc = Channels.newChannel(in);
        try (
                final FileOutputStream fos = new FileOutputStream(path.toFile());
                final FileChannel channel = fos.getChannel()) {
            return channel.transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
    
    public static final long copyMmap(final InputStream in, final Path path) throws IOException {
        try (
                final RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
                final FileChannel channel = file.getChannel()
        ) {
            final MappedByteBuffer out = channel.map(MapMode.READ_WRITE, 0, 1 << 30);
            long bytesRead = 0;
            final byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buffer)) > 0) {
                out.put(buffer);
                bytesRead += n;
                if ((bytesRead & (BUFFER_SIZE << 7) - 1) == 0) {
                    System.out.println("read " + bytesRead / 1000 + " KB");
                }
            }
            channel.truncate(bytesRead);
            return bytesRead;
        }
    }
    
    public static final BufferedReader getReader(final InputStream in) {
        return new BufferedReader(new InputStreamReader(in));
    }
    
    public static final BufferedWriter getWriter(final OutputStream out) {
        return new BufferedWriter(new OutputStreamWriter(out));
    }
    
    public static final Iterable<String> iterable(final BufferedReader reader) {
        return () -> new Iterator<>() {
            
            private @Nullable String line;
            
            @Override
            public boolean hasNext() {
                if (line != null) {
                    return true;
                }
                try {
                    line = reader.readLine();
                    return line != null;
                } catch (final IOException e) {
                    throw ExceptionUtils.atRuntime(e);
                }
            }
            
            @Override
            public @NotNull String next() {
                if (hasNext()) {
                    assert line != null;
                    final @NotNull String next = line;
                    line = null;
                    return next;
                }
                throw new NoSuchElementException();
            }
            
        };
    }
    
    public static final char FILE_NAME_REPLACEMENT_CHAR = '_';
    private static final char[] illegalFileNameChars = {
            '/', '<', '>', ':', '"', '\\', '|', '?',
            '*'
    };
    private static final boolean[] illegalFileNameCharMap = new boolean[1 << Byte.SIZE];
    
    static {
        for (final char c : illegalFileNameChars) {
            illegalFileNameCharMap[c] = true;
        }
    }
    
    public static boolean isLegalFileNameCharacter(final char c) {
        return c > illegalFileNameCharMap.length || !illegalFileNameCharMap[c];
    }
    
    public static final void sanitize(final char[] fileName, final char[] sanitizedFileName,
            final char replacementChar) {
        for (int i = 0; i < sanitizedFileName.length; i++) {
            final char c = fileName[i];
            sanitizedFileName[i] = isLegalFileNameCharacter(c) ? c : replacementChar;
        }
    }
    
    public static final char[] sanitize(final char[] fileName, final char replacementChar) {
        final char[] sanitizedFileName = new char[fileName.length];
        IO.sanitize(fileName, sanitizedFileName, replacementChar);
        return sanitizedFileName;
    }
    
    public static final String sanitize(final String fileName, final char replacementChar) {
        char[] chars = StringUtils.getCharArray(fileName);
        chars = sanitize(chars, replacementChar);
        return StringUtils.newString(chars);
    }
    
    public static final String sanitize(final String fileName) {
        return sanitize(fileName, FILE_NAME_REPLACEMENT_CHAR);
    }
    
    public static final Path sanitize(final Path path) {
        final String fileName = path.getFileName().toString();
        return path.getParent().resolve(sanitize(fileName));
    }
    
    public static final Stream<String> filterUncommentedLines(final Stream<String> lines) {
        return lines.filter(line ->
                !(line.isEmpty()
                        || line.charAt(0) == '#'
                        || line.startsWith("//")));
    }
    
    public static final Stream<String> uncommentedLines(final Path path, final Charset charset)
            throws IOException {
        return filterUncommentedLines(Files.lines(path, charset));
    }
    
    public static final Stream<String> uncommentedLines(final Path path) throws IOException {
        return uncommentedLines(path, DEFAULT_CHARSET);
    }
    
    public static final MappedByteBuffer mmap(final Path path, final long offset, final long size)
            throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
                FileChannel channel = raf.getChannel()) {
            final MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, offset, size);
            ByteBufferUtils.useNativeOrder(buffer);
            return buffer;
        }
    }
    
    public static final MappedByteBuffer mmapAppend(final Path path, final int size) throws
            IOException {
        return mmap(path, path.toFile().length(), size);
    }
    
    public static final MappedByteBuffer mmap(final Path path, final int size) throws IOException {
        return mmap(path, 0, size);
    }
    
    public static final MappedByteBuffer mmap(final Path path) throws IOException {
        return mmap(path, (int) path.toFile().length());
    }
    
    public static final ByteBuffer direct(final int size) {
        return ByteBufferUtils.useNativeOrder(ByteBuffer.allocateDirect(size));
    }
    
    public static final ByteBuffer heap(final int size) {
        return ByteBufferUtils.useNativeOrder(ByteBuffer.allocate(size));
    }
    
    public static final void deleteDirectory(final Path path) throws IOException {
        FileUtils.deleteDirectory(path.toFile());
    }
    
    public static final void createDirectoryOverriding(final Path path) throws IOException {
        deleteDirectory(path);
        Files.createDirectory(path);
    }
    
    public static final byte[] readBytes(final Path path) throws IOException {
        final ByteBuffer buffer = IO.mmap(path);
        final byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return bytes;
    }
    
    public static final InputStream readInputStream(final Path path) throws IOException {
        return new ByteBufferInputStream(IO.mmap(path));
    }
    
    public static final char[] readChars(final Path path, final Charset charset) throws
            IOException {
        return charset.decode(IO.mmap(path)).array();
    }
    
    public static final char[] readChars(final Path path) throws IOException {
        return IO.readChars(path, DEFAULT_CHARSET);
    }
    
    public static final String readString(final Path path, final Charset charset) throws
            IOException {
        //        return StringUtils.newString(IO.readChars(path, charset));
        // FIXME
        return new String(IO.readBytes(path), charset);
    }
    
    public static final String readString(final Path path) throws IOException {
        return IO.readString(path, DEFAULT_CHARSET);
    }
    
    public static final PrintStream printStream(final Path path, final boolean append)
            throws IOException {
        final OpenOption[] options = {
                StandardOpenOption.CREATE,
                append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING
        };
        return new PrintStream(Files.newOutputStream(path, options));
    }
    
    public static final PrintStream printStream(final Path path) throws IOException {
        return IO.printStream(path, false);
    }
    
    public static final void copy(final Path oldPath, final Path newPath) throws IOException {
        final ByteBuffer src = IO.mmap(oldPath);
        final ByteBuffer dest = IO.mmap(newPath, src.capacity());
        dest.put(src);
        ByteBufferUtils.free(src);
        ByteBufferUtils.free(dest);
    }
    
    public static final boolean hasLength(final File file, final long length) {
        return file != null && file.exists() && file.length() == length;
    }
    
    public static final char[][] readMutableLinesAsChars(final Path path, final Charset charset)
            throws IOException {
        return null; // TODO
    }
    
}