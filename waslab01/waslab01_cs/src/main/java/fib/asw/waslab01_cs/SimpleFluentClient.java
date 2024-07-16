package fib.asw.waslab01_cs;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;

//This code uses the Fluent API

public class SimpleFluentClient {

	private static String URI = "http://localhost:8080/waslab01_ss/";

	public final static void main(String[] args) throws Exception {
  	
		String id = Request.post(URI)
		        .bodyForm(Form.form().add("author",  "Juan").add("tweet_text",  "tengo hambre").build())
		        .addHeader("Accept", "text/plain").execute().returnContent().asString();


		System.out.println(id);
        System.out.println(Request.get(URI).addHeader("Accept", "text/plain").execute().returnContent());

		/* Insert code for Task #5 here */
	/*Da un NullPointerException y no logramos ver cuál es la raíz del problema 
      probablemente sea que al crear el tweet no se acaba de guardar el id produciendo un nullPointerException*/
		System.out.println("Se ha borrado el tweet " + id.toString());
	    Request.post(URI).addHeader("Accept", "text/plain").bodyForm(Form.form().add("twid", id).build()).execute();
        
  }
}