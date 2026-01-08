package org.palladiosimulator.simexp.app.console;

public interface GitVersion {
    String TAGS = "${git.tags}";
    String BRANCH = "${git.branch}";
    String DIRTY = "${git.dirty}";
    String REMOTE_ORIGIN_URL = "${git.remote.origin.url}";

    String COMMIT_ID = "${git.commit.id.full}";
    String COMMIT_ID_ABBREV = "${git.commit.id.abbrev}";
    
    String DESCRIBE = "${git.commit.id.describe}";
    String DESCRIBE_SHORT = "${git.commit.id.describe-short}";
    
    String CLOSEST_TAG_NAME = "${git.closest.tag.name}";
    String CLOSEST_TAG_COMMIT_COUNT = "${git.closest.tag.commit.count}";

    String BUILD_VERSION = "${git.build.version}";
    String BUILD_NUMBER = "${git.build.number}";
    String BUILD_NUMBER_UNIQUE = "${git.build.number.unique}";
}