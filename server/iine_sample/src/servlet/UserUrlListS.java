package servlet;

import java.io.IOException;
import javax.servlet.http.*;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class UserUrlListS extends HttpServlet {

    private String user = "Sample User";


    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Key tagKey=null;
        
        resp.getWriter().println("<h3>ユーザごとのリスト</h3>");
       	
    	Key urlKey, userKey, cmp=null;
    	String title, url, tag;
        Date date;
     	//Tags
        try {
            userKey = KeyFactory.createKey("Users", user);
            
        	Query q = new Query("Vote");
            q.addFilter("userKey",
                    Query.FilterOperator.EQUAL,
                    userKey);
            q.addSort("urlKey", Query.SortDirection.DESCENDING).addSort("date", Query.SortDirection.DESCENDING);
            PreparedQuery pq1, pq2;
            pq1 = ds.prepare(q);
        	
            for (Entity x : pq1.asIterable()) {
            	urlKey = (Key)x.getProperty("urlKey");
            	if(!urlKey.equals(cmp)){
            		cmp=urlKey;
            	}
            	else{
            		continue;
            	}
            	date = (Date)x.getProperty("date");
                url = urlKey.getName();
            	Entity SiteList = ds.get(urlKey);
            	title = (String)SiteList.getProperty("title");
				DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
				df.setTimeZone(TimeZone.getTimeZone("JST"));

                resp.getWriter().println(
                        "<a href=" + url + ">" + title + "</a>");
                resp.getWriter().println(
                        "  " + df.format(date) + "に登録</br>");
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
