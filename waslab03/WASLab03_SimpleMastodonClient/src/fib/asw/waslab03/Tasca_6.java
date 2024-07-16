package fib.asw.waslab03;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tasca_6 {

	private static final String LOCALE = "ca";
	
	public static void main(String[] args) {
		try {
			String token = Token.get();
			
			String base = "https://mastodont.cat";
			String endpoint = "/api/v1/trends/tags";
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
            connection.disconnect();            

            JSONArray trends = new JSONArray(response.toString());           
            
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM 'de' yyyy 'a les' HH:mm:ss", new Locale(LOCALE));
    		String now = sdf.format(new Date());
            System.out.println("Els 10 tags més populars a Mastodon [" + now + "]\n");

            
            for (int i = 0; i < Math.min(10, trends.length()); ++i) {
            	JSONObject tagObject = trends.getJSONObject(i);
            	String TAG = tagObject.getString("name");
            	
            	System.out.println("*************************************************");
            	System.out.println( i+1  + ") Tag: " + TAG );
            	System.out.println("*************************************************");

            	
            	String SEndpoint = "/api/v2/search";
            	URL SUrl = new URL(base + SEndpoint + "/?q=" + TAG + "&limit=5&sort_by=created_at");
                
                HttpURLConnection SConnection = (HttpURLConnection) SUrl.openConnection();
                
                SConnection.setRequestMethod("GET");
                SConnection.setRequestProperty("Authorization", "Bearer " + token);
                                
                BufferedReader SReader = new BufferedReader(new InputStreamReader(SConnection.getInputStream()));
                StringBuilder SResponse = new StringBuilder();
                String searchInputLine;

                while ((searchInputLine = SReader.readLine()) != null) {
                    SResponse.append(searchInputLine);
                }

                SReader.close();
                SConnection.disconnect();
                
                JSONObject SResult = new JSONObject(SResponse.toString());
                JSONArray tweets = SResult.getJSONArray("statuses");

                for (int j = 0; j < tweets.length(); j++) {
                    JSONObject toot = tweets.getJSONObject(j);	
                    
                    String authorDisplayName = toot.getJSONObject("account").getString("username");
                    String authorUsername = toot.getJSONObject("account").getString("acct");
                    String tootText = toot.getString("content").replaceAll("<[^>]+>", "").replaceAll("\\n", "");

                    System.out.println("- " + authorDisplayName + " (" + authorUsername + "):" + tootText);
                    System.out.println("-------------------------------------------------");
                }
            	System.out.println("\n");

			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
