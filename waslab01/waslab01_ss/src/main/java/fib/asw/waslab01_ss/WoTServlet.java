package fib.asw.waslab01_ss;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(value = "/")
public class WoTServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TweetDAO tweetDAO;
	private Locale currentLocale = new Locale("en");
    private String ENCODING = "ISO-8859-1";

    public void init() {
    	tweetDAO = new TweetDAO((java.sql.Connection) this.getServletContext().getAttribute("connection"));
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<Tweet> tweets = tweetDAO.getAllTweets();

        if (request.getHeader("Accept").equals("text/plain")) {
        	printPLAINresult(response, tweets);
        }
        else printHTMLresults(response, tweets);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // This method does NOTHING but redirect to the main page
    	String id = request.getParameter("twid");
    	System.out.println(id);
    	if(id== null){
    		try {
    			Long tw = tweetDAO.insertTweet(request.getParameter("author"),request.getParameter("tweet_text"));
    			Cookie c = new Cookie (String.valueOf(tw),sha256(String.valueOf(tw)));
    			response.addCookie(c);
    			id = String.valueOf(tw);
    		} 
    		
    		catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	else {
    		Cookie[] cookies = request.getCookies();
    		for(Cookie c : cookies) {
    			if(c.getValue().equals(sha256(id))) tweetDAO.deleteTweet(Integer.valueOf(id));
    		}
    	}
			
    	
        if(!request.getHeader("Accept").equals("text/plain")) {
        	response.sendRedirect(request.getContextPath());
        }
        else {
            PrintWriter out = response.getWriter();
            response.setContentType ("text/plain");
            out.print(id);
        }
    }
    
    public static String sha256(String base) {
    	
    	try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = md.digest(base.getBytes());

            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02x", b));
            }
            return hexHash.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
		return null;
    }

    private void printHTMLresults (HttpServletResponse response, List<Tweet> tweets) throws IOException {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
        response.setContentType ("text/html");
        response.setCharacterEncoding(ENCODING);

        PrintWriter out = response.getWriter();


        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Wall of Tweets</title>");
        out.println("<link href=\"wot.css\" rel=\"stylesheet\" type=\"text/css\" />");
        out.println("</head>");
        out.println("<body class=\"wallbody\">");
        out.println("<h1>Wall of Tweets</h1>");
        out.println("<div class=\"walltweet\">");
        out.println("<form method=\"post\">");
        out.println("<table border=0 cellpadding=2>");
        out.println("<tr><td>Your name:</td><td><input name=\"author\" type=\"text\" size=70></td><td></td></tr>");
        out.println("<tr><td>Your tweet:</td><td><textarea name=\"tweet_text\" rows=\"2\" cols=\"70\" wrap></textarea></td>");
        out.println("<td><input type=\"submit\" name=\"action\" value=\"Tweet!\"></td></tr>");
        out.println("</table></form></div>");
        String currentDate = "None";
        for (Tweet tweet: tweets) {
            String messDate = dateFormatter.format(tweet.getCreated_at());
            if (!currentDate.equals(messDate)) {
                out.println("<br><h3>...... " + messDate + "</h3>");
                currentDate = messDate;
            }
            out.println("<div class=\"wallitem\">");
            out.println("<h4><em>" + tweet.getAuthor() + "</em> @ "+ timeFormatter.format(tweet.getCreated_at()) +"</h4>");
            out.println("<p>" + tweet.getText() + "</p>");
            out.println("<form action=\"wot\" method=\"post\">");
			out.println("<table border=0 cellpadding=2>");
			out.println("<input type=\"submit\" name=\"action\" value=\"Delete tweet\">");
			out.println("<tr><td><input type=\"hidden\" name=\"twid\" value=" + tweet.getTwid() + "></td></tr>");
			out.println("</table></form>");
            out.println("</div>");
        }
        out.println ( "</body></html>" );
    }
    
    private void printPLAINresult(HttpServletResponse response, List<Tweet> tweets) throws IOException {
    	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
        response.setContentType ("text/plain");
        response.setCharacterEncoding(ENCODING);
        
        PrintWriter out = response.getWriter();


        String currentDate = "None";
        for (Tweet tweet: tweets) {
        out.println("tweet #" + tweet.getTwid() + ": " + tweet.getAuthor()+ ": " + tweet.getText() + " [" +timeFormatter.format(tweet.getCreated_at())+ " ]");
        	
        }
        
    }
}