package fib.asw.waslab03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tasca_5 {

	public static void main(String[] args) {

		try {
			String fib_aswID = "109862447110628983";
			String token = Token.get();
			
			String base = "https://mastodont.cat";
			String endpoint = "/api/v1/accounts/" + fib_aswID + "/statuses";
			URL url = new URL(base + endpoint );
			
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();	
	        connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
	        
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            reader.close();

            JSONArray status = new JSONArray(response.toString());
            
            JSONObject primer = status.getJSONObject(0);
            
            System.out.print(primer.getString("content") + "\n\n" );
            
            connection.disconnect();            
            
            //Boost
            String idtut = primer.getString("id");
            
            endpoint = "/api/v1/statuses/" + idtut + "/reblog";
            URL url1 = new URL(base + endpoint);
            
	        
            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();	

            connection1.setRequestMethod("POST");
            connection1.setRequestProperty("Authorization", "Bearer " + token);
            
            connection1.setDoOutput(true);

            try (OutputStream os = connection1.getOutputStream()) {
                os.write(new byte[0]);
            }
            
            int responseCode = connection1.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Tut boosted");
            } else {
                System.out.println("Error en boost: " + responseCode);
            }
            
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
