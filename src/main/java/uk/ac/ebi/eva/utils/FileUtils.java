package uk.ac.ebi.eva.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import uk.ac.ebi.eva.pipeline.io.GzipLazyResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileUtils {

    public static void validateDirectoryPath(String path, boolean emptyIsValid) throws FileNotFoundException {
        if (emptyIsValid && (path == null || path.isEmpty())) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("Path '" + path + "' doesn't exist.");
        }
        if (!file.isDirectory()) {
            throw new FileNotFoundException("Path '" + path + "' is not a directory.");
        }
    }

    public static URI getPathUri(String path, boolean emptyIsValid) throws FileNotFoundException, URISyntaxException {
        validateDirectoryPath(path, emptyIsValid);
        return URLHelper.createUri(path);
    }

    public static Resource getResource(File file) throws IOException {
        Resource resource;
        if (CompressionHelper.isGzip(file)) {
            resource = new GzipLazyResource(file);
        } else {
            resource = new FileSystemResource(file);
        }
        return resource;
    }
}