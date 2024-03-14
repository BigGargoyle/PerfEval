package cz.cuni.mff.d3s.perfeval.command;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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
     * Name of the git file.
     */
    private static final String GIT_FILE_NAME = ".git";
    /**
     * Prefix for tags.
     */
    private static final String TAGS_PREFIX = "refs/tags/";

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
        pathToRepo = pathToRepo.resolve(GIT_FILE_NAME);
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
        pathToRepo = pathToRepo.resolve(GIT_FILE_NAME);
        try (Git git = Git.open(new File(pathToRepo.toString()))) {
            Repository repository = git.getRepository();
            ObjectId commitId = repository.resolve(version);
            if (commitId == null) {
                return ""; // Commit not found
            }

            // Get all refs (tags and branches)
            Iterable<Ref> refs = git.tagList().call();
            for (Ref ref : refs) {
                ObjectId objectId = ref.getObjectId();
                if (objectId.equals(commitId)) {
                    return ref.getName().replace(TAGS_PREFIX, "");
                }
            }

            return ""; // No tag found for the commit
        } catch (IOException e) {
            throw new IOException("No git file found.", e);
        } catch (GitAPIException e) {
            throw new IOException("Error while getting tags.", e);
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
        pathToRepo = pathToRepo.resolve(GIT_FILE_NAME);
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
