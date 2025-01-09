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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;

import com.google.gson.Gson;

public enum PrismLoader {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(PrismLoader.class);

    private Path prismBinary;

    private class LibraryList {
        public String prefix;
        public String extension;
        public List<String> libraries;
    }

    public synchronized Path load() {
        if (prismBinary != null) {
            return prismBinary;
        }
        // preloadLibraries();
        Path prismPath = Paths.get("/home/zd745/develop/prism/prism-4.8.1-linux64-x86");
        Path prismBinPath = prismPath.resolve("bin");
        prismBinary = prismBinPath.resolve("prism");
        return prismBinary;
    }

    private void preloadLibraries() {
        LOGGER.debug("preloading PRISM libraries");
        Bundle bundle = Platform.getBundle("org.palladiosimulator.simexp.pcm.prism.wrapper");
        try {
            LibraryList libraryList = loadLibraryList(bundle);
            Map<String, URL> resolved = resolveLibraries(bundle, libraryList);
            Path workspaceLibPath = getWorkspaceLibPath();
            Files.createDirectories(workspaceLibPath);
            Map<String, Path> libraryPaths = new HashMap<>();
            for (Map.Entry<String, URL> entry : resolved.entrySet()) {
                URL url = entry.getValue();
                URL resolvedLibrary = FileLocator.resolve(url);
                // Force escaping of invalid characters
                URI prismFileUri = new URI(resolvedLibrary.getProtocol(), resolvedLibrary.getPath(), null);
                // LOGGER.debug(String.format("URI: %s", prismFileUri));
                Path sourceLibraryPath = new File(prismFileUri).toPath();
                Path targetLibraryPath = workspaceLibPath.resolve(sourceLibraryPath.getFileName());
                if (!Files.exists(targetLibraryPath)) {
                    Files.copy(sourceLibraryPath, targetLibraryPath);
                }
                libraryPaths.put(entry.getKey(), targetLibraryPath);
            }
            for (String name : libraryList.libraries) {
                // LOGGER.debug(String.format("load library: %s", System.mapLibraryName(name)));
                Path libraryPath = libraryPaths.get(name);
                System.load(libraryPath.toString());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Path getWorkspaceLibPath() throws URISyntaxException {
        Location instanceLocation = Platform.getInstanceLocation();
        URL instanceUrl = instanceLocation.getURL();
        URI instanceUri = new URI(instanceUrl.getProtocol(), instanceUrl.getPath(), null);
        Path workspacePath = Paths.get(instanceUri);
        Path libFolder = workspacePath.resolve("lib");
        return libFolder;
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

    private Map<String, URL> resolveLibraries(Bundle bundle, LibraryList libraryList) throws IOException {
        Map<String, URL> entries = new HashMap<>();
        for (String name : libraryList.libraries) {
            String libName = String.format("$os$/%s%s%s", libraryList.prefix, name, libraryList.extension);
            org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(libName);
            URL locatedBinary = FileLocator.find(bundle, path, Collections.<String, String> emptyMap());
            if (locatedBinary == null) {
                throw new RuntimeException("unable to resolve: " + libName);
            }
            // LOGGER.debug(String.format("resolved URL: %s", locatedBinary));
            URL fileURL = FileLocator.toFileURL(locatedBinary);
            entries.put(name, fileURL);
        }
        return entries;
    }
}
