package com.org.maven.runner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CloneBitbucketService {

    @Value("${bitbucket.repo.url}")
    private String repoUrl;

    @Value("${bitbucket.clone.dir}")
    private String cloneDir;

    @Value("${bitbucket.username:}") // Optional, for private repos
    private String gitUsername;

    @Value("${bitbucket.token:}") // Optional, for private repos
    private String gitToken;

    public String cloneRepository() {
        try {
            File localPath = new File(cloneDir);

            // Delete existing directory if it already exists
            if (localPath.exists()) {
                deleteDirectory(localPath);
            }

            // Create the Git clone command
            Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(localPath)
                    .setCredentialsProvider(
                            (gitUsername != null && !gitUsername.isEmpty() && gitToken != null && !gitToken.isEmpty())
                                    ? new UsernamePasswordCredentialsProvider(gitUsername, gitToken)
                                    : CredentialsProvider.getDefault()
                    )
                    .call();

            git.close(); // Close Git instance after cloning

            return "Repository cloned successfully to " + cloneDir;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return "Git clone failed: " + e.getMessage();
        }
    }

    // Utility method to delete an existing directory
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}

