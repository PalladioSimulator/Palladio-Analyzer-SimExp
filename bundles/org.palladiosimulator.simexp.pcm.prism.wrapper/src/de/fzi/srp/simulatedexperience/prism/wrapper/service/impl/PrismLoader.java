/** */
package de.fzi.srp.simulatedexperience.prism.wrapper.service.impl;

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
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import org.apache.commons.io.file.PathUtils;
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
        public String architecture;
        public String folder;
    }

    public synchronized Path load() {
        if (prismBinary != null) {
            return prismBinary;
        }
        Path prismPath = preloadLibraries();
        // Path prismPath = Paths.get("/home/zd745/develop/prism/prism-4.8.1-linux64-x86");
        Path prismBinPath = prismPath.resolve("bin");
        prismBinary = prismBinPath.resolve("prism");
        return prismBinary;
    }

    private Path preloadLibraries() {
        LOGGER.debug("preloading PRISM libraries");
        Bundle bundle = Platform.getBundle("org.palladiosimulator.simexp.pcm.prism.wrapper");
        try {
            LibraryList libraryList = loadLibraryList(bundle);
            String folderName = String.format("$os$/%s", libraryList.folder);
            org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(folderName);
            URL locatedFolder = FileLocator.find(bundle, path, Collections.emptyMap());
            if (locatedFolder == null) {
                throw new RuntimeException("unable to resolve: " + folderName);
            }
            URL folderURL = FileLocator.toFileURL(locatedFolder);
            URI folderUri = new URI(folderURL.getProtocol(), folderURL.getPath(), null);
            Path folderPath = Paths.get(folderUri);
            Path workspacePrismPath = getWorkspacePrismPath();
            Path workspacePrismOsPath = workspacePrismPath.resolve(libraryList.architecture);
            Files.createDirectories(workspacePrismOsPath);
            PathUtils.copyDirectory(folderPath, workspacePrismOsPath, StandardCopyOption.REPLACE_EXISTING);
            return workspacePrismOsPath;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Path getWorkspacePath() throws URISyntaxException {
        Location instanceLocation = Platform.getInstanceLocation();
        URL instanceUrl = instanceLocation.getURL();
        URI instanceUri = new URI(instanceUrl.getProtocol(), instanceUrl.getPath(), null);
        Path workspacePath = Paths.get(instanceUri);
        return workspacePath;
    }

    private Path getWorkspacePrismPath() throws URISyntaxException {
        Path workspacePath = getWorkspacePath();
        return workspacePath.resolve("prism");
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
}
