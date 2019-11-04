package com.example.demo.web.controller;

import com.example.demo.Approver.ApproverTreatment;
import com.example.demo.Entity.IssueApp;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class AuthorizationGithub {

    /*
     * Github treatment Class
     * In this class you can find all methods to check and create issues on github project.
     * */

    public static List<IssueApp> getIssuesToCreate(List<IssueApp> issuesAnalizzate, IssueApp[] issuesGithub){

        List<IssueApp> issuesToCreate = new ArrayList<>();
        int count;
        int lung=0;
        for(IssueApp issueAnal:issuesAnalizzate) {
                count=0;
                String control = issueAnal.getTitle();
                String[] prova = control.split("]");
                String finale = prova[0].substring(5); //non ho messo lo spazio dopo ID
                for (int j = 0; j < issuesGithub.length; j++) {
                    String control1 = issuesGithub[j].getTitle();
                    if (control1.startsWith("[ID: ")) {
                        String[] prova1 = control1.split("]");
                        String finale1 = prova1[0].substring(5);
                        if (finale.equals(finale1)) {
                            count++;
                            //System.out.println("non creo issue perchè c'è già ed è aperto ");
                        }
                    }
                }
            if (count == 0) { //k< lung
                IssueApp issue = new IssueApp();
                issue.setTitle(issueAnal.getTitle());
                issue.setState("New");
                issue.setBody(issueAnal.getBody());
                issue.setRemediation(issueAnal.getRemediation());
                issue.setDate(issueAnal.getDate());
                issue.setMd5(issueAnal.getMd5());
                issue.setType(issueAnal.getType());

                if(issueAnal.getVulnerableCode()!=null){
                    issue.setVulnerableCode(issueAnal.getVulnerableCode());
                }
                if(issueAnal.getLevel()!=null){
                    issue.setLevel(issueAnal.getLevel());
                }
                if(issueAnal.getReference()!=null){
                    issue.setReference(issueAnal.getReference());
                }
                issuesToCreate.add(issue);
            }
        }
        return issuesToCreate;
    }
    public static int createIssue(List<IssueApp> issues, String pathUrl, String token) throws JSONException, UnirestException {

        JSONObject jo = new JSONObject();
        JSONArray ja= new JSONArray();
        int size = 0;
        System.out.println("creando issues-----");
        for (IssueApp issue:issues) {
            ja.remove(0);
            jo.put("title", issue.getTitle());
            //per compilare la description degli issue di malware
            if(issue.getRemediation()==null & issue.getLevel()!=null & issue.getVulnerableCode()==null & issue.getReference()==null ){
                jo.put("body", issue.getBody());
                ja.put(issue.getLevel());
                jo.put("labels", ja);
            }
            //per compilare la description degli issue di policies
            else if(issue.getReference()!=null & issue.getRemediation()==null & issue.getLevel()!=null & issue.getVulnerableCode()==null){
                jo.put("body", issue.getBody() + "\n **REFERENCES:**\n "+ issue.getReference());
                ja.put(issue.getLevel());
                jo.put("labels", ja);
            }
            //per compilare la description degli issue di vulnerability
            else{
                if(issue.getVulnerableCode().equals(""))
                    issue.setVulnerableCode(" nessun vulnerable code da mostrare ");
                jo.put("body", issue.getBody() + "\n **REMEDIATION:**\n " + issue.getRemediation() + "\n **Vulnerable Code:** \n" + issue.getVulnerableCode());
                ja.put(issue.getLevel());
                jo.put("labels", ja);

            }

            System.out.println("-----------------------creando issues-----");
            HttpResponse<String> issueResult = Unirest.post("https://api.github.com/repos/"+pathUrl+"/issues")
                    .header("Authorization", "Bearer " + token)
                    .body(jo)
                    .asString();
            System.out.println(issueResult.getBody());
            size++;

        }
        return size;
    }
    public static IssueApp[] getIssuesFromGithub(String token, String pathUrl) throws JSONException, UnirestException {
        //cosi vedo 100 issues e tutti quelli presenti chiusi e aperti
        HttpResponse<String> check = Unirest.get("https://api.github.com/repos/"+pathUrl+"/issues?per_page=100&state=all")
                .header("Authorization", "Bearer " + token)
                .asString();
//        System.out.println(check.getBody());
        JSONArray obj = new JSONArray(check.getBody());
        int n=obj.length();
        IssueApp[] getIssues = new IssueApp[n];
        //per non avere il nullPointer
        for (int i = 0 ; i <getIssues.length; i++)
        {
            getIssues[i] = new IssueApp();
        }
        //per inserire gli issue nel array getIssue
        for(int i=0; i < getIssues.length; i++){
            JSONObject controllo = obj.getJSONObject(i);
            getIssues[i].setTitle(controllo.getString("title"));
            getIssues[i].setState(controllo.getString("state"));
            getIssues[i].setNumIssue(controllo.getString("number"));
        }

        return getIssues;
    }
    public static List<IssueApp> checkIssues(IssueApp[] getIssueFromGithub, List<IssueApp> analisiIssues, String token, String pathUrl) throws UnirestException, JSONException {
        int count=0;
        List<IssueApp> IssueList = new ArrayList<IssueApp>();

        for(int i=0; i<getIssueFromGithub.length;i++) {

            String md5 = "";
            String date = ApproverTreatment.systemDate();
            String control = getIssueFromGithub[i].getTitle();
            String numIssue = getIssueFromGithub[i].getNumIssue();
            if (control.startsWith("[ID: ")) {
                String[] prova = control.split("]");
                String finale = prova[0].substring(5); //non ho messo lo spazio dopo ID
                //System.out.println("UNO"+ finale );
                // System.out.println("STATO : " + getIssueFromGithub[i].getState());
                for (IssueApp analisiIssue:analisiIssues) {
                    String control1 = analisiIssue.getTitle();
                    md5 = analisiIssue.getMd5();
                    String[] prova1 = control1.split("]");
                    String finale1 = prova1[0].substring(5);
                    //  System.out.println("DOS "+ finale1 );
                    if (finale.equals(finale1) & getIssueFromGithub[i].getState().equals("closed")) {
                        //riapri

                        JSONObject jo = new JSONObject();
                        jo.put("state", "open");
                        HttpResponse<String> check = Unirest.patch("https://api.github.com/repos/"+pathUrl+"/issues/" + numIssue)
                                .header("Authorization", "Bearer " + token)
                                .body(jo)
                                .asString();
                        System.out.println("check.getBody()  :" + check.getBody());
                        System.out.println("RIAPRI issue num :" + numIssue);
                        IssueApp issue = new IssueApp();
                        issue.setTitle(getIssueFromGithub[i].getTitle());
                        issue.setMd5(md5);
                        issue.setDate(date);
                        issue.setNumIssue(numIssue);
                        issue.setState("open");
                        IssueList.add(issue);
                    }
                    if (finale.equals(finale1) & getIssueFromGithub[i].getState().equals("open")) {
                        IssueApp issue = new IssueApp();
                        issue.setTitle(getIssueFromGithub[i].getTitle());
                        issue.setMd5(md5);
                        issue.setDate(date);
                        issue.setNumIssue(numIssue);
                        issue.setState("open");
                        IssueList.add(issue);
                        count++;
                        System.out.println("Open Issues on Github :" + count);
                    }
                }

                if (count == 0  & getIssueFromGithub[i].getState().equals("open") ) {
                    //chiudi
                    JSONObject jo = new JSONObject();
                    jo.put("state", "closed");
                    HttpResponse<String> check = Unirest.patch("https://api.github.com/repos/"+pathUrl+"/issues/" + numIssue)
                            .header("Authorization", "Bearer " + token)
                            .body(jo)
                            .asString();
                    System.out.println(check.getBody());
                    System.out.println("CHIUSO!!!!!");
                    IssueApp issue = new IssueApp();
                    issue.setTitle(getIssueFromGithub[i].getTitle());
                    issue.setMd5(md5);
                    issue.setDate(date);
                    issue.setNumIssue(numIssue);
                    issue.setState("closed");
                    IssueList.add(issue);

                }
            }
        }

        return IssueList;
    }
    public static void createLabel(String pathUrl, String token) throws JSONException, UnirestException {
        JSONObject critical = new JSONObject();
        critical.put("name", "Critical" );
        //critical.put("description",);
        critical.put("color","ff0000" );
        HttpResponse<String> labelCriticalCreate = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(critical)
                .asString();
        // System.out.println(labelCriticalCreate.getBody());

        JSONObject high  = new JSONObject();
        high.put("name", "High" );
        //high.put("description",);
        high.put("color","f2a343" );
        HttpResponse<String> labelHighCreate = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(high)
                .asString();
        //System.out.println(labelHighCreate.getBody());

        JSONObject medium  = new JSONObject();
        medium.put("name", "Medium" );
        //medium.put("description",);
        medium.put("color","ffff00" );
        HttpResponse<String> labelMediumCreate = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(medium)
                .asString();
        // System.out.println(labelMediumCreate.getBody());

        JSONObject low  = new JSONObject();
        low.put("name", "Low" );
        //low.put("description",);
        low.put("color","00ff11" );
        HttpResponse<String> labelLowCreate = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(low)
                .asString();
        // System.out.println(labelLowCreate.getBody());

        JSONObject policyViolation  = new JSONObject();
        policyViolation.put("name", "policyViolation" );
        //policyViolation.put("description",);
        policyViolation.put("color","ffe4c4" );
        HttpResponse<String> labelpolicyViolationCreate = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(policyViolation)
                .asString();
        // System.out.println(labelpolicyViolationCreate.getBody());


        JSONObject malware  = new JSONObject();
        malware.put("name", "malware" );
        //malware.put("description",);
        malware.put("color","ff00ff" );
        HttpResponse<String> malwareRis = Unirest.post("https://api.github.com/repos/"+pathUrl+"/labels")
                .header("Authorization", "Bearer " + token)
                .body(malware)
                .asString();
        // System.out.println(malwareRis.getBody());
    }

}
