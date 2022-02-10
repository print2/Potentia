import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FlaskExecutor {

    protected String execFlaskMethod(String methodName, ArrayList<String> parameters){
            String ip = "192.168.43.24:5000";
            String accessUrl = "http://" + ip +"/" + methodName + "/";
            for (String parameter : parameters){
                accessUrl = accessUrl + parameter + "&";
            }
            if(parameters.size() > 0){
                accessUrl = accessUrl.substring(0,accessUrl.length() -1);
            }

            try{
                URL url = new URL(accessUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                if(conn.getResponseCode() != 200){
                    throw new RuntimeException ("Failed : HTTP error code: " + conn.getResponseCode());
                }
                
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                
                String output = br.readLine();
                System.out.println("Output from server ... \n");

                conn.disconnect();

                return output;

            } catch (MalformedURLException e){
                e.printStackTrace();
                return "failed MURL";
            } catch(IOException e){
                e.printStackTrace();
                return "failed IO";
            }
    }
}