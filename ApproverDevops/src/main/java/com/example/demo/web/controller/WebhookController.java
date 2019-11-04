package com.example.demo.web.controller;

import com.example.demo.Approver.ApproverTreatment;
import com.example.demo.Entity.IssueApp;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import org.json.*;


@RestController
public class WebhookController {

    /*
    * Handle treatment Class
    * In this class you can find all the treatment when receives a Webhook.
    * */

    private static final Logger LOG = getLogger(WebhookController.class);

    private String tokenGithub = "eb840ef8d17ac5882996ea78cbe0258d8777884d"; //generare prima come spiegato nel file che spiega il funzionamento del progetto
    private String tokenJenkins="11ed3274355e4e4e1124da82323522578f"; //generare da http://localhost:8080/me/configure
    private String nomeUtenteJenkins="nathaly13";
    private String nomeUrlJenkins="localhost:8080"; //indicare anche la porta
    //String nomeJobJenkins="InsecureBankv2"; //anche questo da pensare come prenderlo per bene! e controllare se non esiste già un job con quel nome
    boolean buildFinish=false;
    String separator = System.getProperty("file.separator");
    String initialPathToDownload="D:"+separator+"programmi"+separator+"workspace"; //percorso fisso! per me è questo
    String lastPartOfThePathApk=separator+"app"+separator+"build"+ separator+"outputs"+separator+"apk"+separator+"debug"+separator+"app-debug.apk";// di default dopo una build l'apk si crea in questo percorso
    String url1="";
    @RequestMapping(value = "/github-webhook", method = RequestMethod.POST)
    public String handle(@RequestHeader("X-GitHub-Event") String event,
                                         @RequestHeader("X-Hub-Signature") String signature,
                                         @RequestBody String payload) throws JSONException, IOException, NoSuchAlgorithmException, UnirestException, InterruptedException, SQLException {


        if (signature == null) {
            LOG.info("No signature given: '{}'", payload);
            System.out.println("No signature :" + HttpStatus.BAD_REQUEST);

        } else {
            LOG.info("Receive an Event: '{}'", event);
            LOG.info("Receive a Signature: '{}'", signature);
            LOG.info("Receive a Payload: '{}'", payload);
            JSONObject obj = new JSONObject(payload.toString());
            String branchName= obj.optString("ref").substring(11); //per prendere il nome del branch
            System.out.println(branchName);
            JSONArray arr = obj.getJSONArray("commits");
            JSONObject jo = obj.getJSONObject("repository");
            String ProjectName= jo.getString("name");
            String pathUrl = jo.getString("full_name");
            System.out.println(pathUrl);
            String ProjectUrl = jo.getString("html_url");
            System.out.println("GUARDA " + pathUrl);
            String add = "";
            String mod = "";
            String nomeFileCommit = "";

            for (int i = 0; i < arr.length(); i++) {
                System.out.println("veamos"+arr.getJSONObject(i));
                if (!arr.getJSONObject(i).getString("removed").equals("[]") & arr.getJSONObject(i).getString("added").equals("[]") & arr.getJSONObject(i).getString("modified").equals("[]")) {
                    System.out.println("non c'è un nuovo file, si è eliminato un file");
                    url1 = null;
                } else if (!arr.getJSONObject(i).getString("added").equals("[]")) {
                    add = arr.getJSONObject(i).getString("added");
                } else if (!arr.getJSONObject(i).getString("modified").equals("[]")) {
                    mod = arr.getJSONObject(i).getString("modified");
                }
                //URL url =new URL ("https://github.com/"+pathUrl+"/archive/"+ branchName+".zip");
                URL url =new URL ("https://api.github.com/repos/"+pathUrl+"/zipball/"+ branchName);
                JenkinsTreatment js = new JenkinsTreatment();
                ApkTreatment apk = new ApkTreatment();
                String tmpDir = apk.downloadAppFromGithub(url, tokenGithub, ProjectName, initialPathToDownload);
               //aqui mismo le paso el path
                String [] resulPathBuildAndGradlew=apk.unzip(tmpDir, initialPathToDownload, ProjectName);
                String pathBuildGradle=resulPathBuildAndGradlew[0];
                String pathGradlew=resulPathBuildAndGradlew[1];
                System.out.println("------ "+ pathGradlew);
                boolean controlloCreate=js.checkNameJob(tokenJenkins, ProjectName, nomeUtenteJenkins, nomeUrlJenkins);
                if(!controlloCreate){
                    js.createJobJenkins(tokenJenkins,ProjectName,pathBuildGradle, pathGradlew, nomeUtenteJenkins, nomeUrlJenkins);
                }
                else {
                    System.out.println("job già esistente con quel nome, vado a fare la build");
                    js.updateJobJenkins(tokenJenkins,ProjectName,pathBuildGradle, pathGradlew, nomeUtenteJenkins, nomeUrlJenkins);
                }
                js.buildJob(tokenJenkins, ProjectName, nomeUtenteJenkins, nomeUrlJenkins);
                buildFinish = js.waitFinishBuild(tokenJenkins , ProjectName, nomeUtenteJenkins, nomeUrlJenkins);
                if (buildFinish==false) {
                    System.out.println("Build fallita "); //probabilmente inutile perchè c'è già il controllo dentro del while nella funzione waitFinishBuild
                } else {
                    System.out.println(" vado a fare check");
                    if (js.checkLastVersionApk(tokenJenkins , ProjectName, nomeUtenteJenkins, nomeUrlJenkins)) // se apk nuovo
                    {
                        //URL url = new URL("http://" + nomeUtenteJenkins + ":" + tokenJenkins + "@" + nomeUrlJenkins + "/job/" + nomeJobJenkins + "/lastSuccessfulBuild/artifact/" + relativePathJenkins);
                        File file = new File(resulPathBuildAndGradlew[0] + lastPartOfThePathApk);
                        ApproverTreatment p = new ApproverTreatment();
                        String token = p.autorizzazione();
//                        System.out.println("File to upload: "+ file.toString());
//                        String md5 = "7659029e6d7823a1a9a64c159065ee64"; // just for testing

                        if (!file.exists()) {
                            System.out.println("attenzione! il file non esiste, l'upload non è eseguito perchè non c'è apk!! ");
                        } else if (file.exists()) { //vengono eseguite tutte le operazioni solo se esiste l'apk
                            String [] checkUpload = apk.uploadAppToApprover(file, token); //da soluzionare il path

                            if (!checkUpload[0].equals("201") || !checkUpload[0].equals("409")) {
                                //201: The apk was uploaded successfully
                                //409: This apk was already uploaded by this user
                                System.out.println("errore nel upload, richiesta non valida o non hai più crediti ");
                            } else { // richiede gli issues e li crea e li controlla solo se l'upload si è eseguito senza problemi

                                List<IssueApp> IssueList = new ArrayList<>();
                                List<IssueApp> IssueToCheck = new ArrayList<>();
                                String md5 = checkUpload[1];
                                AuthorizationGithub au = new AuthorizationGithub();
                                au.createLabel(pathUrl, tokenGithub);

                                List<IssueApp> vulnerability = p.retrieveVulnerability(token, md5);
                                System.out.println("ho preso vulnearbility");
                                for (IssueApp issue : vulnerability) {

                                    IssueToCheck.add(issue);
                                }
                                IssueApp[] issuesFromGithub = au.getIssuesFromGithub(tokenGithub, pathUrl);
                                List<IssueApp> issuesToCreate = au.getIssuesToCreate(vulnerability, issuesFromGithub);

                                if (issuesToCreate.size() <= 0) {
                                    System.out.println("non ho eseguito nessuna azione in quanto gli issue erano già presenti su github");
                                } else {
                                    System.out.println("vado a creare gli issues di vulnerability su Github");
                                    au.createIssue(issuesToCreate, pathUrl, tokenGithub);

                                    for (IssueApp issue : issuesToCreate) {

                                        IssueList.add(issue);
                                    }
                                }
                                List<IssueApp> malware = p.retrieveMalware(token, md5);
                                for (IssueApp issue : malware) {

                                    IssueToCheck.add(issue);
                                }

                                List<IssueApp> issuesToCreateM = au.getIssuesToCreate(malware, issuesFromGithub);
                                if (issuesToCreateM.size() <= 0) {
                                    System.out.println("non ho eseguito nessuna azione in quanto gli issue erano già presenti su github");
                                } else {
                                    System.out.println("vado a creare gli issues di malware su Github");
                                    au.createIssue(issuesToCreateM, pathUrl, tokenGithub);

                                    for (IssueApp issue : issuesToCreateM) {

                                        IssueList.add(issue);
                                    }
                                }


                                List<IssueApp> policies = p.retrievePolicies(token, md5);
                                for (IssueApp issue : policies) {

                                    IssueToCheck.add(issue);
                                }

                                List<IssueApp> issuesToCreateP = au.getIssuesToCreate(policies, issuesFromGithub);
                                if (issuesToCreateP.size() <= 0) {
                                    System.out.println("non ho eseguito nessuna azione in quanto gli issue erano già presenti su github");
                                } else {

                                    System.out.println("vado a creare gli issues di policies su Github");
                                    au.createIssue(issuesToCreateP, pathUrl, tokenGithub);

                                    for (IssueApp issue : issuesToCreateP) {

                                        IssueList.add(issue);
                                    }

                                   // System.out.println("vado a controllare gli issues di policies ");
                                    //au.checkIssues(issuesFromGithub3, totale2, tokenGithub, pathUrl);
                                }
                                System.out.println("Vado a controllare tutti issues su Github");
                                List<IssueApp> checkedissue = new ArrayList<IssueApp>();
                                checkedissue = au.checkIssues(issuesFromGithub, IssueToCheck, tokenGithub, pathUrl);

                                // tutti issue si creano, si modificano a salvare sulla DB
                                DBConnect dbc = new DBConnect();
                                System.out.println("Check if DataBase Approver exists");
                                Boolean exists = dbc.checkDB("approver");
                                if(exists) {

                                    System.out.println("Create all new issues on DataBase");
                                    //Create issues on DataBase
                                    dbc.insertData(IssueList, ProjectName, ProjectUrl);

                                    System.out.println("Create modified issues on DataBase");
                                    //Create modified issues on DataBase
                                    dbc.insertData(checkedissue, ProjectName, ProjectUrl);

                                }
                                // }
                            }// ok upload fai tutte le operazioni!

                        } //ok file exists, fai upload
                    } // APK ok fai l'upload
                }// Build con APK True
            } // vado a scandire il json del webhook
        } //caso positivo di un webhook

        return url1;
    } //fine metodo
} //fine classe
