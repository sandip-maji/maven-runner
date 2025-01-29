package com.org.maven.runner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CloneGitService {

    // Git repository URL and the directory to clone the repository
    @Value("${git.repo.url}")
    private String repoUrl;

    @Value("${git.clone.dir}")
    private String cloneDir;

    @Value("${git.username}")
    private String gitUsername;

    @Value("${git.token}")
    private String gitToken;

    /**
     * Method to clone the repository using JGit with authentication (username + token).
     */
    public String cloneRepository() {
        try {
            CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(gitUsername, gitToken);

            // Clone the repository with authentication
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(new File(cloneDir))
                    .setCredentialsProvider(credentialsProvider)
                    .call();
            return "Repository cloned successfully to " + cloneDir;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return "Git clone failed: " + e.getMessage();
        }
    }
}
