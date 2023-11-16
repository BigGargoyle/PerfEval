package cz.cuni.mff.d3s.perfeval;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GitUtilities {
    public static boolean isRepoClean(Path pathToRepo) throws IOException {

        try (Git git = Git.open(new File(pathToRepo.toString()))){
            Status status = git.status().call();
            return status.isClean();
        } catch (Exception e) {
            throw new IOException("No git file founded.");
        }
    }

    public static RevCommit getLastCommit(Path pathToRepo) throws IOException {
        try (Git git = Git.open(new File(pathToRepo.toString()))){
            Ref head = git.getRepository().exactRef("HEAD");
            ObjectId objectId = head.getObjectId();

            if(objectId==null)
                return null;
            return new RevWalk(git.getRepository()).parseCommit(objectId);

        } catch (Exception e) {
            throw new IOException("No git file founded.");
        }
    }

    public static String getLastCommitTag(Path pathToRepo, String version) throws IOException {
        try (Git git = Git.open(new File(pathToRepo.toString()))){
            ObjectId objectId = git.getRepository().resolve(version);

            if(objectId==null)
                return null;

            Ref tagRef = git.getRepository().getRefDatabase().peel(git.getRepository().getRefDatabase().findRef(objectId.getName()));
            if(tagRef != null)
                return tagRef.getName();
            else
                return "";

        } catch (Exception e) {
            throw new IOException("No git file founded.");
        }
    }
}
