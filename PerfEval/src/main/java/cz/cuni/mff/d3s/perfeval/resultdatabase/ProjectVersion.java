package cz.cuni.mff.d3s.perfeval.resultdatabase;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a version of a project.
 * @param dateOfCommit Date of the commit.
 * @param commitVersionHash Hash of the commit.
 * @param tag Tag of the commit.
 */
public record ProjectVersion(Date dateOfCommit, String commitVersionHash, String tag) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectVersion that = (ProjectVersion) o;
        return Objects.equals(dateOfCommit, that.dateOfCommit)
                && Objects.equals(commitVersionHash, that.commitVersionHash)
                && Objects.equals(tag, that.tag);
    }
    @Override
    public int hashCode() {
        return Objects.hash(dateOfCommit, commitVersionHash, tag);
    }
}
