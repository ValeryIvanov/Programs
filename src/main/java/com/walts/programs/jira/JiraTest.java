package com.walts.programs.jira;


import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class JiraTest {

    public static void main(String[] args) throws URISyntaxException {
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        final URI jiraServerUri = new URI("https://intra.proekspert.ee/jira/");
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "USERNAME", "PASSWORD");
        final NullProgressMonitor pm = new NullProgressMonitor();
        final Issue issue = restClient.getIssueClient().getIssue("ISSUE-NUMBER", pm);
        System.out.println(issue);
    }
}
