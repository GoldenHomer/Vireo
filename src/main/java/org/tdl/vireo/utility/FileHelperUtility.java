package org.tdl.vireo.utility;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.tdl.vireo.Application;

public class FileHelperUtility {

    private final Tika tika = new Tika();

    // TODO: fix problems on Windows!!!
    public String getMimeType(String relativePath) {
        if (relativePath != null) {
            Path path = Paths.get(getPath(relativePath));
            return tika.detect(path.toString());
        } else {
            return "none";
        }
    }
    
    public static String getPath(String relativePath) {
        String path = Application.BASE_PATH + relativePath;
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
    }

}
