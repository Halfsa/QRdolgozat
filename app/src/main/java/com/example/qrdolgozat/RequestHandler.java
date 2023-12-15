package com.example.qrdolgozat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandler {
    private RequestHandler(){}
    private static HttpURLConnection setupConnection(String url) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Accept","application/json");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(1000);
        return connection;
    }
    private static Response getResponse(HttpURLConnection connection) throws IOException{
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (responseCode <400){
            inputStream = connection.getInputStream();
        }else {
            inputStream = connection.getErrorStream();
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String sor = bufferedReader.readLine();
        while (sor != null){
            builder.append(sor);
            sor = bufferedReader.readLine();
        }
        bufferedReader.close();
        inputStream.close();
        return new Response(responseCode,builder.toString());
    }
    private static void addRequestBody(HttpURLConnection connection, String data)throws IOException{
        connection.setRequestProperty("Content-Type","application/json");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(data);
        writer.flush();
        writer.close();
        outputStream.close();
    }
    public static Response get(String url) throws IOException {
        HttpURLConnection connection = setupConnection(url);
        return getResponse(connection);
    }
    public static Response put(String url, String data) throws IOException{
        HttpURLConnection connection = setupConnection(url);
        connection.setRequestMethod("PUT");
        addRequestBody(connection,data);
        return getResponse(connection);
    }
}
