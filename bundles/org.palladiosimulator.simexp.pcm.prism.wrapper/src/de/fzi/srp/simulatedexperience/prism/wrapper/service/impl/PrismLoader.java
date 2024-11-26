/** */
package de.fzi.srp.simulatedexperience.prism.wrapper.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.gson.Gson;

public enum PrismLoader {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(PrismLoader.class);

    private boolean initialized = false;

    private class LibraryList {
        public String prefix;
        public String extension;
        public List<String> libraries;
    }

    public synchronized void load() {
        if (initialized) {
            return;
        }
        initialized = true;
        Bundle bundle = Platform.getBundle("org.palladiosimulator.simexp.pcm.prism.wrapper");
        try {
            LibraryList libraryList = loadLibraryList(bundle);
            List<URL> resolved = resolveLibraries(bundle, libraryList);
            for (URL url : resolved) {
                URL resolvedLibrary = FileLocator.resolve(url);
                // Force escaping of invalid characters
                URI prismFileUri = new URI(resolvedLibrary.getProtocol(), resolvedLibrary.getPath(), null);
                LOGGER.debug(String.format("URI: %s", prismFileUri));
                Path libraryPath = new File(prismFileUri).toPath();
                LOGGER.debug(String.format("library path: %s", libraryPath));
                System.load(libraryPath.toAbsolutePath()
                    .toString());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private LibraryList loadLibraryList(Bundle bundle) throws UnsupportedEncodingException, IOException {
        String stringPath = "/org/palladiosimulator/simexp/pcm/prism/wrapper/library_list.json";
        URL resource = getClass().getResource(stringPath);
        if (resource == null) {
            throw new RuntimeException("unable to find fragment with: " + stringPath);
        }
        try (Reader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8.name())) {
            Gson gson = new Gson();
            LibraryList libraryList = gson.fromJson(reader, LibraryList.class);
            return libraryList;
        }
    }

    private List<URL> resolveLibraries(Bundle bundle, LibraryList libraryList) throws IOException {
        List<URL> entries = new ArrayList<>();
        for (String name : libraryList.libraries) {
            String libName = String.format("%s%s%s", libraryList.prefix, name, libraryList.extension);
            org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(libName);
            URL locatedBinary = FileLocator.find(bundle, path, Collections.<String, String> emptyMap());
            if (locatedBinary == null) {
                throw new RuntimeException("unable to resolve: " + libName);
            }
            LOGGER.debug(String.format("resolved URL: %s", locatedBinary));
            URL fileURL = FileLocator.toFileURL(locatedBinary);
            entries.add(fileURL);
        }
        return entries;
    }
}
