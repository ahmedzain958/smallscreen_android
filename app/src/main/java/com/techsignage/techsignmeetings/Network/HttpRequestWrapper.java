package com.techsignage.techsignmeetings.Network;

import android.content.Context;

import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Classes.FileWrapper;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by heat on 9/12/2017.
 */

public class HttpRequestWrapper {
    JSONObject obj;
    String streamstr;
    String url;
    String content_type;
    Context context;
    String token;
    byte[] stream;
    Integer writeType;

    ArrayList<FileWrapper> files;

    public HttpRequestWrapper(Context context, String url, String content_type, String token,
                        Integer writeType)
    {
        this.url = url;
        this.content_type = content_type;
        this.context = context;
        this.token = token;
        this.writeType = writeType;
    }
    public void setFiles(ArrayList<FileWrapper> files)
    {
        this.files = files;
    }

    public void setStream(byte[] stream)
    {
        this.stream = stream;
    }

    public void setObj(JSONObject obj)
    {
        this.obj = obj;
    }

    public void setStreamstr(String streamstr)
    {
        this.streamstr = streamstr;
    }

    public JSONObject processRequest()
    {
        JSONObject result = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", this.content_type);
            if (token.length() > 0)
                connection.setRequestProperty("Authorization", "Bearer " + token);
            // connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            //connection.setRequestProperty("Authorization", "Bearer " + Token);
            if(this.writeType == 1)
            {
                BufferedWriter wr = new BufferedWriter(
                        new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

                if (this.obj != null)
                    wr.write(this.obj.toString());

                if(this.streamstr != null)
                    wr.write(this.streamstr);

                //wr.close();
                wr.flush();
            }
            else
            {
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                if (this.obj != null)
                    dataOutputStream.writeChars(this.obj.toString());

                if(this.streamstr != null)
                    dataOutputStream.writeChars(this.streamstr);

                if(this.stream != null)
                    dataOutputStream.write(this.stream);

                if(files != null)
                {
                    for (FileWrapper f : files)
                    {
                        Utilities.getFileContent(dataOutputStream, f.formname, f.file, Globals.plainOcStream);
                    }
                }

                if(this.stream != null && this.files != null)
                    dataOutputStream.writeBytes(Globals.SPACER + Globals.BOUNDARY + Globals.SPACER);

                //wr.close();
                dataOutputStream.flush();
            }


            int response = connection.getResponseCode();
            if (response != 200) {
                InputStream inputStream = new BufferedInputStream(connection.getErrorStream());
                StringBuilder sb = new StringBuilder();
                BufferedReader readStream = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = readStream.readLine()) != null)
                    sb.append(line);

                //result = new JSONObject(sb.toString());
                //Log.v("kok", result.toString());
                result = new JSONObject();
                result.put("ArabicMessage", "بيانات غير صحيحة أو غير مكتملة");
                return result;
            }
            //read buffer
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            StringBuilder sb = new StringBuilder();
            BufferedReader readStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = readStream.readLine()) != null)
                sb.append(line);

            result = new JSONObject(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String processRequestString()
    {
        String result = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", this.content_type);
            if (token.length() > 0)
                connection.setRequestProperty("Authorization", "Bearer " + token);
            // connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            //connection.setRequestProperty("Authorization", "Bearer " + Token);
            if(this.writeType == 1)
            {
                BufferedWriter wr = new BufferedWriter(
                        new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

                if (this.obj != null)
                    wr.write(this.obj.toString());

                if(this.streamstr != null)
                    wr.write(this.streamstr);

                //wr.close();
                wr.flush();
            }
            else
            {
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                if (this.obj != null)
                    dataOutputStream.writeChars(this.obj.toString());

                if(this.streamstr != null)
                    dataOutputStream.writeChars(this.streamstr);

                if(this.stream != null)
                    dataOutputStream.write(this.stream);

                if(files != null)
                {
                    for (FileWrapper f : files)
                    {
                        Utilities.getFileContent(dataOutputStream, f.formname, f.file, Globals.plainOcStream);
                    }
                }

                if(this.stream != null && this.files != null)
                    dataOutputStream.writeBytes(Globals.SPACER + Globals.BOUNDARY + Globals.SPACER);

                //wr.close();
                dataOutputStream.flush();
            }


            int response = connection.getResponseCode();
            if (response != 200) {
                InputStream inputStream = new BufferedInputStream(connection.getErrorStream());
                StringBuilder sb = new StringBuilder();
                BufferedReader readStream = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = readStream.readLine()) != null)
                    sb.append(line);

                result = "بيانات غير صحيحة أو غير مكتملة";
                return result;
            }
            //read buffer
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            StringBuilder sb = new StringBuilder();
            BufferedReader readStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = readStream.readLine()) != null)
                sb.append(line);

            result = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
