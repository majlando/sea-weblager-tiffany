package dk.easv.weblager.bll;

import dk.easv.weblager.be.ScanFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lists the .tiff / .tif files in one folder and wraps each as a ScanFile,
 * sorted by filename so pages line up in order. Decoding happens later in
 * the GUI.
 */

public class ScanningManager {

    private final Path scanFolder;

    public ScanningManager(Path scanFolder) {
        this.scanFolder = scanFolder;
    }

    public Path getScanFolder() {
        return scanFolder;
    }

    /** All TIFF files in the scan folder, sorted by filename. */
    public List<ScanFile> loadFiles() {
        if (!Files.isDirectory(scanFolder)) {
            throw new IllegalStateException(
                    "Scan folder not found: " + scanFolder.toAbsolutePath());
        }

        List<Path> tiffs = new ArrayList<>();
        try (Stream<Path> stream = Files.list(scanFolder)) {
            for (Path p : stream.toList()) {
                if (Files.isRegularFile(p) && isTiff(p)) {
                    tiffs.add(p);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list scan folder", e);
        }

        // Sort by filename so the pages line up in order.
        tiffs.sort(Comparator.comparing(p -> p.getFileName().toString()));

        List<ScanFile> result = new ArrayList<>();
        for (int i = 0; i < tiffs.size(); i++) {
            result.add(new ScanFile(i, tiffs.get(i)));
        }
        return result;
    }

    /** True if the filename ends with .tiff or .tif. */
    public static boolean isTiff(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        return name.endsWith(".tiff") || name.endsWith(".tif");
    }
}
