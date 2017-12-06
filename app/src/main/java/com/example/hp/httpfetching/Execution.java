package com.example.hp.httpfetching;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HP on 05-Nov-17.
 */

public class Execution
{
    public Execution()
    {
    }
    public String makeServiceCall(String reqUrl)
    {

        String response=null;
        try
        {
            URL url=new URL(reqUrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in=new BufferedInputStream(conn.getInputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder sb=new StringBuilder();
            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append('\n');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            response=sb.toString();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
