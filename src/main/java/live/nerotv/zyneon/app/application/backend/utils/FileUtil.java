package live.nerotv.zyneon.app.application.backend.utils;

import java.io.File;

public class FileUtil {

    @SuppressWarnings("all")
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
