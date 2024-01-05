package cz.cuni.mff.d3s.perfeval.command;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

/**
 * Class containing utilities for working with git.
 */
public class GitUtilities {

    /**
     * Checks if the repository is clean.
     *
     * @param pathToRepo Path to the repository.
     * @return True if the repository is clean, false otherwise.
     * @throws IOException If the repository is not found.
     */
    public static boolean isRepoClean(Path pathToRepo) throws IOException {

        try (Git git = Git.open(new File(pathToRepo.toString()))) {
            Status status = git.status().call();
            return status.isClean();
        } catch (Exception e) {
            throw new IOException("No git file founded.", e);
        }
    }

    /**
     * Gets the last commit of the repository.
     *
     * @param pathToRepo Path to the repository.
     * @return Last commit of the repository.
     * @throws IOException If the repository is not found.
     */
    public static RevCommit getLastCommit(Path pathToRepo) throws IOException {
        try (Git git = Git.open(new File(pathToRepo.toString()))) {
            Ref head = git.getRepository().exactRef("HEAD");
            ObjectId objectId = head.getObjectId();

            if (objectId == null) {
                return null;
            }
            return new RevWalk(git.getRepository()).parseCommit(objectId);

        } catch (Exception e) {
            throw new IOException("No git file founded.", e);
        }
    }

    /**
     * Gets the last commit tag of the repository.
     *
     * @param pathToRepo Path to the repository.
     * @param version    Version of the repository.
     * @return Last commit tag of the repository.
     * @throws IOException If the repository is not found.
     */
    public static String getLastCommitTag(Path pathToRepo, String version) throws IOException {
        try (Git git = Git.open(new File(pathToRepo.toString()))) {
            ObjectId objectId = git.getRepository().resolve(version);

            if (objectId == null) {
                return null;
            }

            Ref tagRef = git.getRepository().getRefDatabase().peel(git.getRepository().getRefDatabase().findRef(objectId.getName()));
            if (tagRef != null) {
                return tagRef.getName();
            } else {
                return "";
            }

        } catch (Exception e) {
            throw new IOException("No git file founded.", e);
        }
    }

    /**
     * Gets the date of the commit.
     *
     * @param pathToRepo Path to the repository.
     * @param commitHash Hash of the commit.
     * @return Date of the commit.
     */
    public static Date getCommitDate(Path pathToRepo, String commitHash) {
        try (Git git = Git.open(new File(pathToRepo.toString()))) {
            ObjectId objectId = git.getRepository().resolve(commitHash);

            if (objectId == null) {
                return null;
            }

            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(objectId);
            return commit.getAuthorIdent().getWhen();

        } catch (Exception e) {
            return null;
        }
    }

}
