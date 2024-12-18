package org.palladiosimulator.simexp.pcm.prism.entity;

public class PrismContext {
    private final String kind;
    private final String propertyFileContent;

    private String moduleFileContent;

    public PrismContext(String kind, String moduleFileContent, String propertyFileContent) {
        this.kind = kind;
        this.moduleFileContent = moduleFileContent;
        this.propertyFileContent = propertyFileContent;
    }

    public String getKind() {
        return kind;
    }

    public String getPropertyFileContent() {
        return propertyFileContent;
    }

    public String getModuleFileContent() {
        return moduleFileContent;
    }

    public void resolveAndSubstitute(String symbolToReplace, String value) {
        moduleFileContent = moduleFileContent.replaceAll(symbolToReplace, value);
    }
}
