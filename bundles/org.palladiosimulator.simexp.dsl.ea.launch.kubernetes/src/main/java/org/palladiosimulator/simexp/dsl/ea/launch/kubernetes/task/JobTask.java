package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

import java.util.ArrayList;
import java.util.List;

public class JobTask {
    public String id;
    public String command;

    public static class WorkspaceEntry {
        public enum Kind {
            FILE, ARCHIVE
        };

        public WorkspaceEntry(WorkspaceEntry.Kind kind, String name) {
            this(kind, name, "", new byte[0]);
        }

        public WorkspaceEntry(WorkspaceEntry.Kind kind, String name, String compressor, byte[] content) {
            this.kind = kind;
            this.name = name;
            this.contentSize = content.length;
            this.compressor = compressor;
            this.content = content;
        }

        public WorkspaceEntry.Kind kind;
        public String name;
        public int contentSize;
        public String compressor;
        public byte[] content;
    }

    public String workspacePath;
    public String workspaceArgument;
    public List<WorkspaceEntry> workspaceEntries = new ArrayList<>();
    public String launcherName;
}