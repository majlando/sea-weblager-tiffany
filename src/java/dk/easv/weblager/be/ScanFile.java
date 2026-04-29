package dk.easv.weblager.be;

import java.nio.file.Path;

/**
 * Business entity for one scanned TIFF page.
 */
public class ScanFile {

    private final int index;
    private final Path path;

    public ScanFile(int index, Path path) {
        this.index = index;
        this.path = path;
    }

    public int getIndex()  { return index; }
    public Path getPath()  { return path; }

    /** ListView's default cell calls toString(), so this controls how rows look. */
    @Override
    public String toString() {
        return String.format("%04d  %s", index + 1, path.getFileName());
    }
}
