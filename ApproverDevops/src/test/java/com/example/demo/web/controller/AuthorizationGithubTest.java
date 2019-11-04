package com.example.demo.web.controller;

import com.example.demo.Entity.IssueApp;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import java.util.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorizationGithubTest {

    /*
    * For testing this class we use repositories: testing and ApproverSDK wish are public
    *  https://github.com/Sbouamri/testing
    *  https://github.com/Sbouamri/ApproverSDK
    * */

    @Test
    public void getIssuesToCreateTest_OK() {
        List<IssueApp> issuesanali = new ArrayList<>();
        IssueApp[] githubissue = new IssueApp[2];
        IssueApp issue = new IssueApp();
        IssueApp issue1 = new IssueApp();
        IssueApp issue2 = new IssueApp();
        IssueApp issue3 = new IssueApp();
        issue.setTitle("[ID: VULNERABEL_ANALYSIS]");
        issue1.setTitle("[ID: MALWARE_ANALYSIS]");
        issue2.setTitle("[ID: MALWARE_ANALYSIS]");
        issue3.setTitle("[ID: POLICIES_ANALYSIS]");
        issuesanali.add(issue);
        issuesanali.add(issue1);
        githubissue[0] = issue2;
        githubissue[1] = issue3;

        assertEquals(1, AuthorizationGithub.getIssuesToCreate(issuesanali,githubissue).size());
    }

    @Test
    public void getIssuesToCreateTest_EMPTY() {
        List<IssueApp> issuesanali = new ArrayList<>();
        IssueApp[] githubissue = new IssueApp[2];

        assertEquals(0, AuthorizationGithub.getIssuesToCreate(issuesanali,githubissue).size());
    }

    @Test
    public void createIssueTest_OK()throws JSONException, UnirestException {
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/testing";
        List<IssueApp> issuesanali = new ArrayList<>();
        IssueApp issue = new IssueApp();
        issue.setTitle("[ID: TEST CREATE ISSUE]");
        issue.setVulnerableCode("");
        issuesanali.add(issue);
        assertEquals(1,AuthorizationGithub.createIssue(issuesanali,pathUrl,tokenGithub));
    }

    @Test
    public void createIssueTest_EMPTY()throws JSONException, UnirestException {
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/testing";
        List<IssueApp> issuesanali = new ArrayList<>();
        assertEquals(0,AuthorizationGithub.createIssue(issuesanali,pathUrl,tokenGithub));
    }

    @Test
    public void getIssuesFromGithubTest_OK() throws JSONException, UnirestException{
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/testing";
        assertTrue(AuthorizationGithub.getIssuesFromGithub(tokenGithub,pathUrl).length == 2);
    }

    @Test
    public void getIssuesFromGithubTest_Issue_Not_Exists() throws JSONException, UnirestException{
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/ApproverSDK";
        assertFalse(AuthorizationGithub.getIssuesFromGithub(tokenGithub,pathUrl).length == 1);
    }

    /*
    * This method tests both Reopen and Close issues!!
    * The repository "testing" has two issues, the first one wish is closed with the Title "[ID: TEST CREATE ISSUE]"
    * and the second one wish is open with the Title "[ID: TEST CLOSE ISSUE]" or other title
    * */
    @Test
    public void checkIssuesTest_Reopen_Close_Issue() throws JSONException, UnirestException{
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/testing";
        List<IssueApp> issuesanalis = new ArrayList<>();
        IssueApp issue = new IssueApp();
        issue.setTitle("[ID: TEST CREATE ISSUE]");
        issue.setMd5("123456");
        issue.setVulnerableCode("");
        issuesanalis.add(issue);
        IssueApp[] githubissue = AuthorizationGithub.getIssuesFromGithub(tokenGithub,pathUrl);
        assertEquals(2,AuthorizationGithub.checkIssues(githubissue,issuesanalis,tokenGithub,pathUrl).size());
    }
}