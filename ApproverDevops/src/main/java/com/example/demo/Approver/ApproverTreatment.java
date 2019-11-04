package com.example.demo.Approver;
import java.io.*;
import java.sql.Date;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.example.demo.Entity.IssueApp;
import org.json.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.text.*;

public class ApproverTreatment {

    /*
     * Approver treatment Class
     * In this class you can find all methods to get Vulnerability, Policies and Malware analysis from Approver.
     * */

    public static String autorizzazione() throws IOException, JSONException {
        URL url = new URL("https://auth-server.talos-sec.com/oauth/token");
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        String token;
        try{
            String urlParameters = "grant_type=password&username=progetto8&password=PT8RrpGwuAMs8dd7";
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Basic YnJvd3Nlcjo=" );
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            try {
                wr.write(postData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder content;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String line;
            content = new StringBuilder();
            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            System.out.println(content.toString());
            JSONObject obj = new JSONObject(content.toString());
            token = obj.getString("access_token");
            System.out.println("ciao " + token);
        }
        finally {conn.disconnect();}

        return token;
    }

    public static String convertDate(String date){

        String dateFormat = "dd/MM/yyyy hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String systemDate(){

        String dateFormat = "dd/MM/yyyy hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(Long.parseLong(date));
        return simpleDateFormat.format(calendar.getTime());
    }


    public static List<IssueApp> retrieveVulnerability (String token, String md5) throws UnirestException, JSONException, InterruptedException {
     int ris=0;
     List<IssueApp> listIssue = new ArrayList<>();
   if (token != null || !token.isEmpty() && md5 != null || !md5.isEmpty()) {
       if(token == "" || md5 == "") {
           listIssue = null;
       }else {
           while (ris != 200) {
               Thread.sleep(3000);

               HttpResponse<String> vulnerabilityResult = Unirest.get("https://vulnerability-checker.talos-sec.com/apks/" + md5)
                       .header("Authorization", "Bearer " + token)
                       .asString();
               ris = vulnerabilityResult.getStatus();

               System.out.println(" Vediamo ris retrieve vulnerability  " + ris);

               if (ris != 200) {
                   System.out.println("aspetta! vulnerability  non ancora pronte!");
               } else {
                   JSONObject obj = new JSONObject(vulnerabilityResult.getBody().toString());
                   JSONObject time = obj.getJSONObject("analysis_finish_time");
                   String milliSeconds = time.getString("$date");
                   String analysisTime = convertDate(milliSeconds);
                   String type = "Vulnerability";
                   System.out.println("analyseTime:  " + analysisTime);
                   System.out.println("Application:  " + md5);
                   System.out.println("System Date:  " + systemDate());
                   JSONObject details = obj.getJSONObject("details");
                   String vulnerableCode = null;

                   Iterator<?> iterator = details.keys();

                   while (iterator.hasNext()) {

                       IssueApp arrayIssue = new IssueApp();
                       String key = (String) iterator.next();
                       JSONObject issue = details.getJSONObject(key);
                       String name = issue.getString("name");
                       String level = issue.getString("level");
                       JSONArray ja = issue.optJSONArray("vulnerable_code");
                       String description = issue.getString("description");
                       String remediation = issue.optString("remediation");
                       String vulnerability_id = issue.getString("vulnerability_id");
                       String initField1 = "";

                       String initField2 = "";

                       JSONObject jsonObject = null;

                       //= String.valueOf(ja);

                       arrayIssue.setVulnerableCode("");


                       for (int l = 0; l < ja.length(); l++) {

                           vulnerableCode = "";

                           if (!issue.getString("level").equals("Info")) {

                               jsonObject = ja.optJSONObject(l);

                               Iterator<?> iterator2 = jsonObject.keys();

                               initField1 = (String) iterator2.next();

                               initField2 = jsonObject.getString(initField1);

                               //System.out.println("** nombre " + name);

                               while (iterator2.hasNext()) {

                                   String key2 = (String) iterator2.next();

                                   String info = jsonObject.getString(key2);

                                   vulnerableCode += "\n " + key2 + "| " + info;

                                   // System.out.println("_---" + key2 + " ++++ " + info);

                               }

                               // String prova=jsonObject.optString(String.valueOf(i));


                           }

                           //System.out.print( initField1 + " | " + initField2 + "\n ------------ | ------------- " );

                           // System.out.println(vulnerableCode);
                           arrayIssue.setVulnerableCode(arrayIssue.getVulnerableCode() + "\n " + initField1 + " | " + initField2 + "\n ------------ | ------------- " + vulnerableCode + "\n");

                       }
                       if (!issue.getString("level").equals("Info")) {
                           if (remediation.equals("") || remediation.isEmpty())
                               remediation = " nessuna remediation consigliata ";
                           arrayIssue.setTitle("[ID: " + vulnerability_id + "]  " + name);
                           arrayIssue.setBody(description);
                           arrayIssue.setRemediation(remediation);
                           arrayIssue.setDate(analysisTime);
                           arrayIssue.setMd5(md5);
                           arrayIssue.setType(type);
                           arrayIssue.setLevel(level);
                           listIssue.add(arrayIssue);
                           System.out.println("Title: " + arrayIssue.getTitle());
                           System.out.println("Level: " + arrayIssue.getLevel());
                       }
                   }

               }
           }
       }
   }
    return listIssue;
}

    public static List<IssueApp> retrievePolicies (String token, String md5) throws UnirestException, JSONException, InterruptedException {

        List<IssueApp> listIssue = new ArrayList<>();
        int ris = 0;
        String links="";
//    IssueApp[] arrayIssue=null;
        if (token != null||token.isEmpty() && md5 != null||md5.isEmpty()) {
        if(token == "" || md5 == "") {
            listIssue = null;
        }else {
            while (ris != 200) {
                Thread.sleep(2000);


                HttpResponse<String> policiesResult = Unirest.get("https://policy-checker.talos-sec.com/apks/" + md5)
                        .header("Authorization", "Bearer " + token)
                        .asString();
                ris = policiesResult.getStatus();
                System.out.println(" Vediamo ris retrieve policy " + ris);
                if (ris != 200) {
                    System.out.println("aspetta! policies non ancora pronte!");
                } else {
                    //System.out.println(policiesResult.getBody());
                    JSONArray arr = new JSONArray(policiesResult.getBody().toString());
                    for (int i = 0; i < arr.length(); i++) {
                        links = "";
                        JSONObject satisfied = arr.getJSONObject(i);
                        // System.out.println(satisfied);
                        // mi servono solo quelli con satified a false
                        if (satisfied.getString("satisfied").equals("false")) {
                            JSONObject policy = satisfied.getJSONObject("policy");
                            String title = policy.getString("title");
                            String description = policy.getString("description");
                            JSONObject reference = policy.getJSONObject("references");
                            String text = reference.getString("text");
                            JSONObject link = reference.getJSONObject("links");
                            Iterator<?> iterator2 = link.keys();
                            while (iterator2.hasNext()) {

                                String key = (String) iterator2.next();
                                links = links + link.getString(key) + "\n";
                            }
                            IssueApp issue = new IssueApp();
                            issue.setTitle("[ID: " + title + "]");
                            issue.setBody(description);
                            issue.setDate(systemDate());
                            issue.setMd5(md5);
                            issue.setType("Policy");

                            if (text.equals("") || text.isEmpty()) text = " niente da mostrare ";

                            if (links.equals("") || links.isEmpty()) links = " niente da mostrare ";

                            issue.setReference("\n Text | Links \n " + "------------ | ------------- \n " + text + " | " + links);

                            issue.setLevel("policyViolation");
                            // j++;
                            listIssue.add(issue);
                        }
                    }

                }
            }
        }
    }
    return listIssue;
}


    public static List<IssueApp> retrieveMalware (String token, String md5) throws UnirestException, JSONException, InterruptedException {
        int ris = 0;
        List<IssueApp> listIssue = new ArrayList<>();
        //IssueApp[] issue = null;
        if (token != null||token.isEmpty() && md5 != null||md5.isEmpty()) {
            if (token == "" || md5 == "") {
                listIssue = null;
            } else {
                while (ris != 200) {
                    Thread.sleep(2000);

                    HttpResponse<String> malwareResult = Unirest.get("https://malware-analysis.talos-sec.com/apks/" + md5)

                            .header("Authorization", "Bearer " + token)

                            .asString();

                    ris = malwareResult.getStatus();

                    System.out.println(" Vediamo ris retrieve malware " + ris);

                    if (ris != 200) {

                        System.out.println("aspetta! malware non ancora pronte!");

                    } else {

                        JSONObject obj = new JSONObject(malwareResult.getBody().toString());

                        int scannersDetected = Integer.parseInt(obj.getString("scannersDetected"));

                        System.out.println("scannersDetected :" + scannersDetected);

                        if (scannersDetected > 0) {

                            JSONArray arr = obj.getJSONArray("scannerResults");

                            int n = 0;

                            //per inizializzare il numero totale di issue che andrò a creare in quanto mi serve solo 1 issue in totale

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject mn = arr.optJSONObject(i);

                            String malwareName = mn.optString("malwareName");

                            if (!malwareName.isEmpty()) {

                                n++;

                            }
                        }
                            String[] motoriMalware = new String[n];

                            int j = 0;

                            for (int i = 0; i < arr.length(); i++) {

                                JSONObject mn = arr.optJSONObject(i);

                                String malwareName = mn.optString("malwareName");

                                if (!malwareName.isEmpty()) { // & j < n

                                    String scannerName = mn.optString("scannerName");

                                    motoriMalware[j] = scannerName + " | " + malwareName;

                                    j++;

                                }


                            }

                            String risMalware = "";

                            for (int i = 0; i < motoriMalware.length; i++) {

                                risMalware = risMalware + motoriMalware[i] + "\n";

                            }

                            IssueApp issue = new IssueApp();
                            issue.setTitle("[ID: MALWARE_ANALYSIS]");
                            issue.setBody("fai un controllo! ci sono " + n + " malware: \n " + "\n scanner name | malware name" + "\n ------------ | ------------- \n" + risMalware);
                            issue.setDate(systemDate());
                            issue.setMd5(md5);
                            issue.setType("Malware");
                            issue.setLevel("malware");

                            System.out.println(issue.getBody());
                            listIssue.add(issue);
                        }

                    }
                    System.out.println(listIssue.size());
                }
            }
        }
        return listIssue;
    }


}