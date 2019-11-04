package com.example.demo.web.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkTreatment {

    /*
     * APK treatment Class
     * In this class you can find all methods to get the project from Github and upload it on Approver platform.
     * */

    public static String getZip(String tokenGithub, URL url) throws UnirestException, IOException {

        System.out.println(url);
        Unirest.setHttpClient(org.apache.http.impl.client.HttpClients.custom()
                .disableRedirectHandling()
                .build());
        HttpResponse<String> getLinkToDonwload = Unirest.get(url.toString())
                .header("Authorization", "Bearer " + tokenGithub)
                .asString();
        //System.out.println(getLinkToDonwload.getBody()+ " " +getLinkToDonwload.getStatus());
       // System.out.println("locaation " +getLinkToDonwload.getHeaders().get("Location"));
        List<String> urltodownload= getLinkToDonwload.getHeaders().get("Location");
        String apkUrl=urltodownload.get(0);

        System.out.println("apkUrl   :"+ apkUrl);

        System.out.println(apkUrl);

        return apkUrl;

    }

    public static String downloadAppFromGithub(URL getzipUrl,String token, String projectName, String initialPathToDownload) throws UnirestException, IOException {
        String separator = System.getProperty("file.separator");
        String apkUrl= getZip(token, getzipUrl);
        URL url =new URL(apkUrl);

        String tempDir=initialPathToDownload+separator+projectName+".zip";
        File dstFile = new File (tempDir);
        try {
            FileUtils.copyURLToFile(url, dstFile.getAbsoluteFile());
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println(" DIRECTORY " +tempDir);
        return tempDir;
    }

    public static String[] uploadAppToApprover(File file, String token){
        String result[]= new String [2];
        HttpResponse<String> uploadResult = null;
        try {
            uploadResult = Unirest.post("https://apk-storage.talos-sec.com/store")
                    .header("Authorization", "Bearer " +token)
                    .field("apkFile", file)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        List<String> urlMd5= uploadResult.getHeaders().get("Location");
        String[] url = urlMd5.get(0).split("/");
        String md5=url[url.length-1];
        System.out.println("MD5: " + md5);
        System.out.println("Status: " +uploadResult.getStatus());
        System.out.println(uploadResult.getStatusText() + " " + uploadResult.getStatus() + " " +uploadResult.getBody());
        result[0] = String.valueOf(uploadResult.getStatus());
        result[1] = md5;
        return result;
    }

    public static String[] unzip(String filename, String zipPath, String nomeJob) throws UnirestException {

        File srcFile = new File(filename);
        String pathGradlew="";
        String pathBuildGradle="";
        String separator = System.getProperty("file.separator");
        // create a directory with the same name to which the contents will be extracted
        // String zipPath = filename.substring(0, filename.length()-4);
        File temp = new File(zipPath);
        temp.mkdir();
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(srcFile);
            // get an enumeration of the ZIP file entries
            Enumeration<?> e = zipFile.entries();

            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                System.out.println("entry.getName() " +entry.getName());

                File destinationPath = new File(zipPath, entry.getName());
                System.out.println("destinationPath " +destinationPath.toString());

                //create parent directories
                destinationPath.getParentFile().mkdirs();
                System.out.println("destinationPath.getParentFile().mkdirs() " +destinationPath.getParentFile().toString());

                // if the entry is a directory extract it

                if (entry.isDirectory()) {
                    continue;
                }
                else {

                    String [] nr= destinationPath.toString().split(Pattern.quote(separator));
                    String fileGradle=nr[nr.length-2]+separator+ nr[nr.length-1];
                    System.out.println("guarda " +fileGradle);
                    if(fileGradle.equals(nomeJob+ separator+"gradlew.bat"))
                    {
                        pathGradlew=destinationPath.toString();
                        System.out.println("percorso gradlew " + pathGradlew);
                    }
                    if(fileGradle.equals(nomeJob+ separator+"build.gradle"))
                    {
                        pathBuildGradle=destinationPath.toString();
                        System.out.println("percorso build " + pathBuildGradle);
                    }
                    System.out.println("Extracting file: " + destinationPath);
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    int b;
                    byte buffer[] = new byte[4096];
                    FileOutputStream fos = new FileOutputStream(destinationPath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
                    while ((b = bis.read(buffer, 0, 4096)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.close();
                    bis.close();

                }
            }
        }
        catch (IOException ioe) {
            System.out.println("Error opening zip file" + ioe);
        }
        finally {
            try {
                if (zipFile!=null) {
                    zipFile.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing zip file" + ioe);
            }
        }
        String result[]= new String [2];
        if (pathBuildGradle.isEmpty()){
            System.out.println("file build.gradle non esiste nella cartella ! controlla ! ");
        }else {
            pathBuildGradle=pathBuildGradle.substring(0, pathBuildGradle.length()-13);
            System.out.println("prova " +pathBuildGradle);
            pathBuildGradle=pathBuildGradle.replaceAll("\\\\", "/" );
            System.out.println("prova " +pathBuildGradle);
            result[0]=pathBuildGradle;
        }
        if(pathGradlew.isEmpty()){
            System.out.println("file gradlew.bat non esiste nella cartella ! controlla ! ");
        } else {
            pathGradlew=pathGradlew.substring(0, pathGradlew.length()-12); // per prendere solo il percorso senza il nome del file perchè cosi lo vuole jenkins

            pathGradlew=pathGradlew.replaceAll("\\\\" , "/" );
            System.out.println("prov..a " +pathGradlew);
            result[1]=pathGradlew;
        }

        return result ;
    }
}
