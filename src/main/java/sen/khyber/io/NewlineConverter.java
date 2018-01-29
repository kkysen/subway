package sen.khyber.io;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Khyber Sen on 1/29/2018.
 *
 * @author Khyber Sen
 */
public class NewlineConverter {
    
    private final @Getter boolean useLF;
    
    public NewlineConverter(final boolean useLF) {
        this.useLF = useLF;
    }
    
    public NewlineConverter() {
        this(true);
    }
    
    public final Process convertFile(final Path path) throws IOException {
        //        System.out.println(path);
        final Process process = new ProcessBuilder(useLF ? "dos2unix" : "unix2dos", path.toString())
                .inheritIO()
                .start();
        process.info().commandLine().ifPresent(System.out::println);
        return process;
    }
    
    public final Stream<Process> convertDirectory(final Path dir,
            final Predicate<? super Path> filter) throws
            IOException {
        Stream<Path> paths = Files.walk(dir)
                .parallel()
                .filter(Files::isRegularFile);
        if (filter != null) {
            paths = paths.filter(filter);
        }
        return paths
                .map(path -> {
                    try {
                        final Process process = convertFile(path);
                        process.waitFor();
                        return process;
                    } catch (final IOException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }
    
    public static void main(final String[] args) throws IOException {
        System.out.println(
                new NewlineConverter()
                        .convertDirectory(Paths.get("").toAbsolutePath(), path ->
                                StreamSupport.stream(path.spliterator(), false)
                                        .noneMatch(dir ->
                                                Files.isDirectory(dir)
                                                        && dir
                                                        .getFileName()
                                                        .toString()
                                                        .equals(".git")
                                        )
                        )
                        .collect(Collectors.toList())
                        .size());
    }
    
}