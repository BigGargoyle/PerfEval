package cz.cuni.mff.d3s.perfeval.resultDatabase;

import java.util.Date;
import java.util.Objects;

public record ProjectVersion(Date dateOfCommit, String commitVersionHash, String tag) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectVersion that = (ProjectVersion) o;
        return Objects.equals(dateOfCommit, that.dateOfCommit) &&
                Objects.equals(commitVersionHash, that.commitVersionHash) &&
                Objects.equals(tag, that.tag);
    }
}
