package live.nerotv.zyneon.app.application.backend.utils.frame;

import java.io.File;
import java.nio.file.Path;

public class StringUtil {

    public static String getURLFromFile(String path) {
        return "file://"+path;
    }

    public static String getUrlFromFile(Path path) {
        return getURLFromFile(path.toString());
    }

    public static String getURLFromFile(File file) {
        return getURLFromFile(file.getAbsolutePath());
    }

    public static String addHyphensToUUID(String uuidString) {
        StringBuilder sb = new StringBuilder(uuidString);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return sb.toString();
    }
}
