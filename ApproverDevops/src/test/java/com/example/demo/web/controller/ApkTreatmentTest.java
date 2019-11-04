package com.example.demo.web.controller;

import com.example.demo.Approver.ApproverTreatment;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class ApkTreatmentTest {


    @Test
    public void getZipTest() throws IOException, UnirestException {
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/InsecureBankv2";
        String branchName= "master";
        URL url =new URL ("https://api.github.com/repos/"+pathUrl+"/zipball/"+ branchName);
        String apkUrl= ApkTreatment.getZip(tokenGithub, url);
        assertNotNull(apkUrl);
    }

    @Test
    public void downloadAppFromGithubTest_File_Exists() throws IOException, UnirestException {
        String projectName = "InsecureBankv2";
        String tokenGithub = "f71fb4d1fb40852f459e07698f23684bdb86c9cc";
        String pathUrl = "Sbouamri/InsecureBankv2";
        String branchName= "master";
        URL url =new URL ("https://api.github.com/repos/"+pathUrl+"/zipball/"+ branchName);
        String separator = System.getProperty("file.separator");
        String initialPathToDownload="D:"+separator+"programmi"+separator+"workspace"; //percorso fisso! per me è questo
        //File file = new File(ApkTreatment.downloadAppFromGithub(url, tokenGithub,projectName,initialPathToDownload));
        String tempdir = ApkTreatment.downloadAppFromGithub(url, tokenGithub,projectName,initialPathToDownload);
        System.out.println("tempdir  :" + tempdir);  //D:\programmi\workspace\InsecureBankv2.zip
        File file = new File(tempdir);
        assertTrue(file.exists());
    }

    @Test
    public void unzipTest() throws UnirestException {
        String projectName = "InsecureBankv2";
        String tempdir = "D:\\programmi\\workspace\\InsecureBankv2.zip";
        String separator = System.getProperty("file.separator");
        String initialPathToDownload="D:"+separator+"programmi"+separator+"workspace"; //percorso fisso! per me è questo
        String [] resulPathBuildAndGradlew=ApkTreatment.unzip(tempdir, initialPathToDownload, projectName);
        assertNotNull(resulPathBuildAndGradlew);
    }

    @Test
    public void uploadAppToApproverTest_Apk_OK() throws IOException, UnirestException, JSONException {
        String TOKEN = ApproverTreatment.autorizzazione();
        File file= new File("C:\\Users\\hp\\Downloads\\app-release.apk"); // You can replace "app-release.apk" with an other apk!
        assertEquals("201", ApkTreatment.uploadAppToApprover(file, TOKEN)[0]);
    }

    @Test
    public void uploadAppToApproverTest_Apk_Exists() throws IOException, UnirestException, JSONException {
        String TOKEN = ApproverTreatment.autorizzazione();
        File file= new File("C:\\Users\\hp\\Downloads\\Lancher3.apk");  // You can replace "Lancher3.apk" with an other apk that exists on Approver application!!
        assertEquals("409", ApkTreatment.uploadAppToApprover(file, TOKEN)[0]);
    }

}