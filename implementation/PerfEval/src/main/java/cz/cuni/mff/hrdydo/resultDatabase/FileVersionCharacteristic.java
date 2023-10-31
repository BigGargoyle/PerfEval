package cz.cuni.mff.hrdydo.resultDatabase;

import java.io.StringBufferInputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

public record FileVersionCharacteristic(Date dateOfCommit, String commitVersionHash, String tag) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileVersionCharacteristic that = (FileVersionCharacteristic) o;
        return Objects.equals(dateOfCommit, that.dateOfCommit) &&
                Objects.equals(commitVersionHash, that.commitVersionHash) &&
                Objects.equals(tag, that.tag);
    }
}
