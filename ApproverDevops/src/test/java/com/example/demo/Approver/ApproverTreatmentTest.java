package com.example.demo.Approver;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

public class ApproverTreatmentTest {

    //public static String TOKEN = null;
    public static final String MD5 = "f7151dc57342da0df0fcad87cc085cca";

    @Test
    public void autorizzazioneTest() throws IOException, JSONException {
        String tokenTest = ApproverTreatment.autorizzazione();
        assertNotNull(tokenTest);
    }

    @Test
    public void convertDateTest() {
        String timeStp = "1569831746916";
        String convDate = ApproverTreatment.convertDate(timeStp);
        assertNotNull(convDate);
    }

    @Test
    public void systemDateTest() {
        String sysDate = ApproverTreatment.systemDate();
        assertNotNull(sysDate);
    }


    /**
     * Test of retrieveVulnerability method, of class ApproverTraitment.
     */
    @Test
    public void retrieveVulnerabilityTest_Null() throws IOException, JSONException, InterruptedException, UnirestException {
        String token = "";
        String md5 = "";
        assertNull(ApproverTreatment.retrieveVulnerability(token,md5));

    }

    @Test
    public void retrieveVulnerabilityTest_OK() throws IOException, JSONException, InterruptedException, UnirestException {
        String TOKEN = ApproverTreatment.autorizzazione();
        assertNotNull(ApproverTreatment.retrieveVulnerability(TOKEN,MD5));

    }

    @Test
    public void retrievePoliciesTest_Null() throws InterruptedException, UnirestException, JSONException {
        String token = "";
        String md5 = "";
        assertNull(ApproverTreatment.retrievePolicies(token,md5));
    }

    @Test
    public void retrievePoliciesTest_OK() throws InterruptedException, UnirestException, JSONException, IOException {
        String TOKEN = ApproverTreatment.autorizzazione();
        assertNotNull(ApproverTreatment.retrievePolicies(TOKEN,MD5));
    }

    @Test
    public void retrieveMalwareTest_Null() throws InterruptedException, UnirestException, JSONException {
        String token = "";
        String md5 = "";
        assertNull(ApproverTreatment.retrieveMalware(token,md5));
    }

    @Test
    public void retrieveMalwareTest_OK() throws InterruptedException, UnirestException, JSONException, IOException {
        String TOKEN = ApproverTreatment.autorizzazione();
        assertNotNull(ApproverTreatment.retrieveMalware(TOKEN,MD5));
    }


}