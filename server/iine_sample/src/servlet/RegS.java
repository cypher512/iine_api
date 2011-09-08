package servlet;

import java.io.IOException;

import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;


@SuppressWarnings("serial")
public class RegS extends HttpServlet {
    private static final Logger log = Logger.getLogger(RegS.class.getName());

    // Hard code the SiteList board name for simplicity.  Could support
    // multiple boards by getting this from the URL.
    private String user = "Sample User";

    public static String escapeHtmlChars(String inStr) {
        return inStr.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Display a web form for creating new messages.
        resp.getWriter().println(
            "<p>Post a SiteList:</p>" +
            "<form action=\"/RegS\" method=\"POST\">" +
            "<label for=\"url\">URL:</label>" +
            "<input type=\"text\" name=\"url\" id=\"url\" /><br />" +
            "<label for=\"title\">Title:</label>" +
            "<input type=\"text\" name=\"title\" id=\"title\" /><br />" +
           "<input type=\"submit\" />" +
            "</form>");

    }

    public void doPost(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {

        // Save the SiteList and update the board count in a
        // transaction, retrying up to 3 times.

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        String title = req.getParameter("title");
        String url = req.getParameter("url");
        Date date = new Date();

        int retries = 3;
        boolean success = false;
     	//SiteList
        Transaction txn = null;
        Key urlKey=null;
        while (!success && retries > 0) {
            --retries;
           try {
                txn = ds.beginTransaction();

                Entity SiteList;
            	
                try {
					urlKey = KeyFactory.createKey("SiteList", url);
					SiteList = ds.get(urlKey);
				} catch (Exception e) {
	                SiteList = new Entity("SiteList", url);
	                SiteList.setProperty("title", title);
	                SiteList.setProperty("url", url);
                    SiteList.setProperty("count", 0L);
	                urlKey = ds.put(SiteList);
				}

                long count = (Long) SiteList.getProperty("count");
                ++count;
                SiteList.setProperty("count", count);
                ds.put(SiteList);

                log.info("Posting msg, updating count to " + count +
                         "; " + retries + " retries remaining");

                txn.commit();
            	

                // Break out of retry loop.
                success = true;

            } catch (DatastoreFailureException e) {
                // Allow retry to occur.
            }
            finally{
            	if(txn.isActive())
            		txn.rollback();
            }
        	
        }
        	
        retries = 3;
        success = false;
    	//Users
        while (!success && retries > 0) {
            --retries;
            try {
                txn = ds.beginTransaction();

                Key userKey;
                Entity Users;
            	
                try {
                    userKey = KeyFactory.createKey("Users", user);
                    Users = ds.get(userKey);		//なにもしない

                } catch (EntityNotFoundException e) {
                    Users = new Entity("Users", user);
	                userKey = ds.put(Users);
	                txn.commit();
                }

                // Break out of retry loop.
                success = true;

            } catch (DatastoreFailureException e) {
                // Allow retry to occur.
            }
            finally{
            	if(txn.isActive())
            		txn.rollback();
            }
        	
        }
        retries = 3;
        success = false;
    	//Vote
        while (!success && retries > 0) {
            --retries;
            try {
                txn = ds.beginTransaction();

                Key userKey;
                Entity vote;
            	
            	vote = new Entity("Vote");
                userKey = KeyFactory.createKey("Users", user);
				urlKey = KeyFactory.createKey("SiteList", url);
            	vote.setProperty("userKey", userKey);
            	vote.setProperty("urlKey", urlKey);
            	vote.setProperty("date", date);
            	ds.put(vote);
          	
                txn.commit();

                // Break out of retry loop.
                success = true;

            } catch (DatastoreFailureException e) {
                // Allow retry to occur.
            }
            finally{
            	if(txn.isActive())
            		txn.rollback();
            }
        	
        }        	
        if (!success) {
            resp.getWriter().println
                ("<p>A new SiteList could not be posted.  Try again later." +
                 "<a href=\"/\">Return to the board.</a></p>");
        } else {
            resp.sendRedirect("/");
        }
    }
}
