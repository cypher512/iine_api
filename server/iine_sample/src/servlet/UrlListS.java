package servlet;

import java.io.IOException;
import javax.servlet.http.*;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class UrlListS extends HttpServlet {

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

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

       	
    	String title, url;

        resp.getWriter().println("<h3>URL一覧</h3>");
        try {
        	
        	Query q = new Query("SiteList");
        	
            q.addSort("count", Query.SortDirection.DESCENDING);
        	
            PreparedQuery pq = ds.prepare(q);
            for (Entity x : pq.asIterable()) {
                url = (String)x.getProperty("url");
                title = (String)x.getProperty("title");
                resp.getWriter().println(
                        "<a href=" + url + ">" + title + "</a><br>");

                
            }
	        resp.getWriter().println(
	                "<a href=/>" + "トップページに戻る" + "</a>");
        	
		} catch (Exception e) {
			String message = "";
		
		}
        
            


    }


}
