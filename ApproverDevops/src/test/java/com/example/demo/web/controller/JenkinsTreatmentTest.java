package com.example.demo.web.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class JenkinsTreatmentTest {

    /*
    * For testing this class you have to change the path of "pathBuildGradle" and "pathGradlew" and put the one
    * on the your computer.
    * You can execute at the first the method "downloadAppFromGithubTest_File_Exists()" on the class
    * "ApkTreatmentTest.java" to get a new directory (new paths)
    * */

    @Test
    public void checkNameJobTest() throws JSONException, UnirestException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2222";
        boolean createControl=JenkinsTreatment.checkNameJob(tokenJenkins, projectName, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals(Boolean.FALSE, createControl);
    }

    @Test
    public void createJobJenkinsTest() throws IOException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2";
        String pathBuildGradle="D:/programmi/workspace/Sbouamri-InsecureBankv2-9163ef488d7e1b33dcc4fcbee397acf61f2f3f3a/InsecureBankv2";
        String pathGradlew="D:/programmi/workspace/Sbouamri-InsecureBankv2-9163ef488d7e1b33dcc4fcbee397acf61f2f3f3a/InsecureBankv2";
        boolean jobCreated= JenkinsTreatment.createJobJenkins(tokenJenkins,projectName,pathBuildGradle, pathGradlew, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals(Boolean.TRUE,jobCreated);
    }

    @Test
    public void updateJobJenkins() throws IOException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2";
        String pathBuildGradle="D:/programmi/workspace/Sbouamri-InsecureBankv2-c7a5d792aaa62e5b66726386d4f617d4a71018b6/InsecureBankv2";
        String pathGradlew="D:/programmi/workspace/Sbouamri-InsecureBankv2-c7a5d792aaa62e5b66726386d4f617d4a71018b6/InsecureBankv2";
        boolean jobUpdated= JenkinsTreatment.updateJobJenkins(tokenJenkins,projectName,pathBuildGradle, pathGradlew, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals(Boolean.TRUE,jobUpdated);
    }

    @Test
    public void buildJobTest() throws UnirestException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2";
        String buildJob=JenkinsTreatment.buildJob(tokenJenkins, projectName, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals("OK", buildJob);
    }

    @Test
    public void waitFinishBuildTest() throws InterruptedException, UnirestException, JSONException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2";
        boolean buildFinish=JenkinsTreatment.waitFinishBuild(tokenJenkins, projectName, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals(Boolean.TRUE, buildFinish);
    }

    @Test
    public void checkLastVersionApkTest() throws InterruptedException, UnirestException, JSONException {
        String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
        String nomeUtenteJenkins="sbouamri";
        String nomeUrlJenkins="localhost:8080";
        String projectName="InsecureBankv2";
        boolean buildFinish=JenkinsTreatment.checkLastVersionApk(tokenJenkins, projectName, nomeUtenteJenkins, nomeUrlJenkins);
        assertEquals(Boolean.TRUE, buildFinish);
    }
}