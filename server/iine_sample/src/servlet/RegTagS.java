package servlet;

import java.io.IOException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

@SuppressWarnings("serial")
public class RegTagS extends HttpServlet {

    // Hard code the Tags board name for simplicity.  Could support
    // multiple boards by getting this from the URL.

    public static String escapeHtmlChars(String inStr) {
        return inStr.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {
        resp.setContentType("text/html;charset=UTF-8");


        // Display information about a Tags board and its messages.


        // Display a web form for creating new messages.
        resp.getWriter().println(
            "<p>Post a Tag:</p>" +
            "<form action=\"/RegTagS\" method=\"POST\">" +
            "<label for=\"tag\">Tag:</label>" +
            "<input type=\"text\" name=\"tag\" id=\"tag\" /><br />" +
            "<label for=\"url\">URL:</label>" +
            "<input type=\"text\" name=\"url\" id=\"url\" /><br />" +
           "<input type=\"submit\" />" +
            "</form>");

    }

    public void doPost(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {

        // Save the Tags and update the board count in a
        // transaction, retrying up to 3 times.

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        String url = req.getParameter("url");
        String tag = req.getParameter("tag");

        int retries = 3;
        boolean success = false;
        Key tagKey=null;
        Entity Tags=null;
        	
     	//Tags
        while (!success && retries > 0) {
            --retries;
            Transaction txn=null;
            try {
                
                txn = ds.beginTransaction();
                try {
					tagKey = KeyFactory.createKey("Tags", tag);
					Tags = ds.get(tagKey);
				} catch (Exception e) {
	                Tags = new Entity("Tags", tag);
	                tagKey = ds.put(Tags);
				}
                
                
           	
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
    	//TagBelongs
        while (!success && retries > 0) {
            --retries;
            Transaction txn=null;
            try {
           	
                txn = ds.beginTransaction();
                Entity tagBelong=null;
            	try{
	            	tagBelong = new Entity("TagBelongs");
	            	tagBelong.setProperty("tagKey", tagKey);
					Key	urlKey = KeyFactory.createKey("SiteList", url);
	            	tagBelong.setProperty("urlKey", urlKey);
            	}
            	catch(DatastoreFailureException e) {
            		
            	}
            	ds.put(tagBelong);
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
                ("<p>A new Tags could not be posted.  Try again later." +
                 "<a href=\"/\">Return to the board.</a></p>");
        } else {
            resp.sendRedirect("/");
        }
    }
}
