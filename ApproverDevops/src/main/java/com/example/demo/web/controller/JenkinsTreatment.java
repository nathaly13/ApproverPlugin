package com.example.demo.web.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.*;


public class JenkinsTreatment {

    /*
     * Jenkins treatment Class
     * In this class you can find all methods to create a job on Jenkins and build it.
     * */

    public static boolean checkNameJob(String tokenJenkins, String ProjectName, String nomeUtenteJenkins, String nomeUrlJenkins) throws UnirestException, JSONException {
        HttpResponse<String> checkNameJob = Unirest.get("http://"+nomeUtenteJenkins+":"+tokenJenkins+"@"+nomeUrlJenkins+"/api/json?pretty=true")
                .asString();
        System.out.println("Print checkNameJob: "+checkNameJob.getStatus());
        System.out.println("Print checkNameJob.getBody(): "+checkNameJob.getBody());
        JSONObject obj = new JSONObject(checkNameJob.getBody().toString());
        JSONArray jsonArray= obj.getJSONArray("jobs");
        JSONObject ja=null;
        String nameJob="";
        //boolean tmp=true;
        boolean check=false;
        for (int i=0; i <jsonArray.length(); i++){
            ja=jsonArray.getJSONObject(i);
            nameJob=ja.optString("name");
            System.out.println("name job " +nameJob);
            if(nameJob.equals(ProjectName)){
                check=true; // non devo chiamare quindi la funzione createJobJenkins
                System.out.println("ho trovato un job con lo stesso nome");
            }
        }
        return check;
    }
    //controllare se job già esistente !! xk se già esistente con quel nome da errore!
    public static boolean createJobJenkins(String tokenJenkins, String ProjectName, String pathBuildGradle, String pathGradlew, String nomeUtenteJenkins, String nomeUrlJenkins) throws IOException {
        File input= new File(System.getProperty("user.dir")+"\\config.xml");
        File config= new File(System.getProperty("user.dir")+"\\config.xml");
        String separator = System.getProperty("file.separator");
        config.createNewFile();
        boolean jobCreate = false;
        try {
            System.out.println("holaaa " + pathBuildGradle);
            System.out.println("holaaa " + pathGradlew);
            Path path = Paths.get(System.getProperty("user.dir")+separator+"config_temp.xml");
            //Path path = Paths.get(new ClassPathResource("templates/config_temp.xml").getURI());
            Path path2 = Paths.get(System.getProperty("user.dir")+separator+"config.xml");
            Stream<String> lines = Files.lines(path);
            List <String> replaced = lines.map(line -> line.replaceAll("<wrapperLocation></wrapperLocation>", "<wrapperLocation>"+pathGradlew+"</wrapperLocation>"))
                    .map(line -> line.replaceAll("<rootBuildScriptDir></rootBuildScriptDir>", "<rootBuildScriptDir>"+pathBuildGradle+"</rootBuildScriptDir>"))
                    .collect(Collectors.toList());
            Files.write(path2, replaced);
            lines.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        PostMethod post = new PostMethod("http://"+nomeUtenteJenkins+":"+tokenJenkins+"@"+nomeUrlJenkins+"/createItem?name="+ProjectName);
        post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(input), input.length()));
        post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        // Execute request
        try {
            int result = httpclient.executeMethod(post);
            // status code
            System.out.println("Response status code: " + result);
            // response body
            System.out.println("Response body: ");
            //System.out.println(post.getResponseBodyAsString());
            jobCreate = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Release current connection

            post.releaseConnection();
        }
        return jobCreate;
    }

    public static boolean updateJobJenkins(String tokenJenkins, String ProjectName, String pathBuildGradle, String pathGradlew, String nomeUtenteJenkins, String nomeUrlJenkins) throws IOException {
        File input= new File(System.getProperty("user.dir")+"\\config.xml");
        File config= new File(System.getProperty("user.dir")+"\\config.xml");
        String separator = System.getProperty("file.separator");
        config.createNewFile();
        boolean jobUpdated = false;
        try {
            System.out.println("holaaa " + pathBuildGradle);
            System.out.println("holaaa " + pathGradlew);
            Path path = Paths.get(System.getProperty("user.dir")+separator+"config_temp.xml");
            //Path path = Paths.get(new ClassPathResource("templates/config_temp.xml").getURI());
            Path path2 = Paths.get(System.getProperty("user.dir")+separator+"config.xml");
            Stream<String> lines = Files.lines(path);
            List <String> replaced = lines.map(line -> line.replaceAll("<wrapperLocation></wrapperLocation>", "<wrapperLocation>"+pathGradlew+"</wrapperLocation>"))
                    .map(line -> line.replaceAll("<rootBuildScriptDir></rootBuildScriptDir>", "<rootBuildScriptDir>"+pathBuildGradle+"</rootBuildScriptDir>"))
                    .collect(Collectors.toList());
            Files.write(path2, replaced);
            lines.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        PostMethod post = new PostMethod("http://"+nomeUtenteJenkins+":"+tokenJenkins+"@"+nomeUrlJenkins+"/job/"+ProjectName+"/config.xml");
        post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(input), input.length()));
        post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
        // Get HTTP client
        HttpClient httpclient = new HttpClient();
        // Execute request
        try {
            int result = httpclient.executeMethod(post);
            // status code
            System.out.println("Response status code: " + result);
            // response body
            System.out.println("Response body: ");
            //System.out.println(post.getResponseBodyAsString());
            jobUpdated = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Release current connection

            post.releaseConnection();
        }
        return jobUpdated;
    }

    public static String buildJob (String tokenJenkins, String nomeProgetto, String nomeUtenteJenkins, String nomeUrlJenkins) throws UnirestException {
        String buildJob ="KO";
        HttpResponse<String> build = Unirest.get("http://" + nomeUtenteJenkins + ":" + tokenJenkins + "@" + nomeUrlJenkins + "/job/" + nomeProgetto + "/build?token=123")
                .asString();
        System.out.println("ok build! " + build.getStatus());
        if(build.getStatus() == 201)
            buildJob="OK";
        return buildJob;

    }

    public static  boolean waitFinishBuild(String tokenJenkins, String nomeProgetto, String nomeUtenteJenkins, String nomeUrlJenkins) throws UnirestException, JSONException, InterruptedException {
        Thread.sleep(5000);
        boolean buildFinish=false;
        boolean versionBuild=false;
        do {
            Thread.sleep(5000);
            HttpResponse<String> checkResult2;
            do {
                checkResult2 = Unirest.get("http://" + nomeUtenteJenkins + ":" + tokenJenkins + "@" + nomeUrlJenkins + "/job/" + nomeProgetto + "/lastCompletedBuild/api/json?pretty=true")
                        .asString();
                System.out.println(checkResult2.getStatus() +" dentro do " + checkResult2.getBody());
            }while(checkResult2.getStatus()==404);
            System.out.println(checkResult2.getStatus() +" fuori do " + checkResult2.getBody());
            JSONObject obj2= new JSONObject(checkResult2.getBody().toString());
            String timeStamp= obj2.optString("timestamp");
            String result=obj2.optString("result");
            long pp=Long.parseLong(timeStamp);
            Calendar jenkins = Calendar.getInstance();
            jenkins.setTimeInMillis(pp);
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            System.out.println("****** now min "+ now.get(Calendar.MINUTE)+ " jenkins min " + jenkins.get(Calendar.MINUTE));

            HttpResponse<String> checkResult = Unirest.get("http://" + nomeUtenteJenkins + ":" + tokenJenkins + "@" + nomeUrlJenkins + "/job/" + nomeProgetto + "/api/json?pretty=true")
                    .asString();
            JSONObject obj = new JSONObject(checkResult.getBody().toString());
            JSONObject check3 = obj.getJSONObject("lastCompletedBuild");
            String numVersion3 = check3.optString("number");
            JSONObject check = obj.getJSONObject("lastBuild");
            String numVersion = check.optString("number");
            System.out.println("lastbuild " + numVersion + " lastcompletebuild " + numVersion3);
            if (!numVersion.equals(numVersion3)) { //build non completata! aspetta-> dormi
                versionBuild = false;
                System.out.println("aspetta build non ancora finita!");
            } else {  //build completata
                System.out.println("build finita ");
                versionBuild = true;
                //potrebbe essere finita ma non con successo
            }
            jenkins.set(Calendar.SECOND, 0);
            jenkins.set(Calendar.MILLISECOND, 0);
            jenkins.set(Calendar.SECOND, 0);
            jenkins.set(Calendar.MILLISECOND, 0);



            if(jenkins.get(Calendar.DAY_OF_MONTH)==now.get(Calendar.DAY_OF_MONTH) && jenkins.get(Calendar.MONTH)==now.get(Calendar.MONTH) &&
                    jenkins.get(Calendar.YEAR)==now.get(Calendar.YEAR) ){//&& jenkins.get(Calendar.HOUR)==now.get(Calendar.HOUR) ) {
                int minuti= Math.abs(now.get(Calendar.MINUTE) - jenkins.get(Calendar.MINUTE));
                System.out.println("****** " +minuti);
                if(minuti==0 || minuti==1){
                    if(jenkins.get(Calendar.HOUR)==now.get(Calendar.HOUR)) {
                        if (result.equals("SUCCESS")) {
                            buildFinish = true; // se true controllo che sia succ sennò break con stampa
                        } else if (result.equals("FAILURE")) {
                            System.out.println("build fallita");
                            break;
                        }
                    }
                }
                if(minuti==59){
                    int ore=Math.abs((now.get(Calendar.HOUR)-jenkins.get(Calendar.HOUR)));
                    if(ore==1) {
                        if (result.equals("SUCCESS")) {
                            buildFinish = true; // se true controllo che sia succ sennò break con stampa
                        } else if (result.equals("FAILURE")) {
                            System.out.println("build fallita");
                            break;
                        }
                    }
                }
            }
            else buildFinish=false;

//
            Thread.sleep(5000);
        } while (buildFinish==false && versionBuild==false);// finchè buildFinisg=false rifai e rifai!

        return buildFinish;
    }

    public static boolean checkLastVersionApk(String tokenJenkins, String nomeProgetto, String nomeUtenteJenkins, String nomeUrlJenkins) throws UnirestException, JSONException, InterruptedException {
        HttpResponse<String> checkResult = Unirest.get("http://"+nomeUtenteJenkins+":"+tokenJenkins+"@"+nomeUrlJenkins+"/job/"+nomeProgetto+"/api/json?pretty=true")
                .asString();
        Thread.sleep(5000);
        JSONObject obj = new JSONObject(checkResult.getBody().toString());
        JSONObject check = obj.getJSONObject("lastBuild");
        String numVersion= check.optString("number");
        JSONObject check2 = obj.getJSONObject("lastSuccessfulBuild");
        String numVersion2= check2.optString("number");
        boolean result=false;
        System.out.println("lastBuild " + numVersion + " lastBuild succ " + numVersion2);
        if(numVersion.equals(numVersion2))
            result=true;
        else {
            System.out.println("l'ultima build non ha generato apk");
            result=false;
        }
        return result;
    }

}
