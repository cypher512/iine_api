package servlet;

import java.io.IOException;
import javax.servlet.http.*;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class TagListS extends HttpServlet {


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

        String tag = req.getParameter("tag");

        Key tagKey=null;
        
        resp.getWriter().println("<h3>" + tag +" タグによるリスト</h3>");
       	
    	Key urlKey;
    	String title, url;
     	//Tags
        try {
			tagKey = KeyFactory.createKey("Tags", tag);
        	
        	Query q = new Query("TagBelongs");
        	
            q.addFilter("tagKey",
                    Query.FilterOperator.EQUAL,
                    tagKey);
            PreparedQuery pq1, pq2;
        	pq1 = ds.prepare(q);
            for (Entity x : pq1.asIterable()) {
                urlKey = (Key)x.getProperty("urlKey");
                url = urlKey.getName();
            	Entity SiteList = ds.get(urlKey);
            	title = (String)SiteList.getProperty("title");
                
                resp.getWriter().println(
                        "<a href=" + url + ">" + title + "</a><br>");
            	
                q = new Query("TagBelongs");
                q.addFilter("urlKey",
                        Query.FilterOperator.EQUAL,
                        urlKey);
                pq2 = ds.prepare(q);
            	resp.getWriter().println("<ul>");
                for (Entity y : pq2.asIterable()) {
                	tagKey = (Key)y.getProperty("tagKey");
                	tag = tagKey.getName();
	                resp.getWriter().println(
	                        "<li>" + "<a href=" + "TagListS?tag=" + tag + ">" + tag + "</a>" + "</li>");
                	
                }
            	resp.getWriter().println("</ul>");
                
            }
	        resp.getWriter().println(
	                "<a href=/>" + "トップページに戻る" + "</a>");
        	
		} catch (Exception e) {
			String message = "";
		
		}

    }


}
