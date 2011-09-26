package servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.*;

import net.arnx.jsonic.JSON;


import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
* TagEditSクラス
* タグ編集画面のサーバ側
**/

@SuppressWarnings("serial")
public class TagEditS extends HttpServlet {
	private Key urlKey;
	class Title{
		public String title;
        public ArrayList<Tags> tagArray;
	}	
	class Tags{
		public String tag;
	};
	
    public void doGet(HttpServletRequest req,
                      HttpServletResponse resp)
        throws IOException {

        resp.setContentType("text/html;charset=UTF-8");									//UTF-8にする
        	
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

        Key tagKey=null;
        
       	String tag2mod, cmd;
        PreparedQuery pq2;
     	String tmp = req.getParameter("urlKey");										//パラメータを取り出す
     	cmd = req.getParameter("cmd");
     	tag2mod = req.getParameter("tag");
        	
        int retries = 3;																//3回までRetry
        boolean success = false;
        Entity tagEntity=null;
        	
     	//Tagの更新
    	if(cmd!=null && cmd.equals("add")){												//追加する場合
	        while (!success && retries > 0) {
	            --retries;
	            Transaction txn=null;
	            try {
	            	urlKey = KeyFactory.stringToKey(tmp);								//urlKeyをKey型に
	                
	                txn = ds.beginTransaction();										//Transaction開始
	                try {
						tagKey = KeyFactory.createKey("tagEntity", tag2mod);			//タグ名がすでにあるとき
						tagEntity = ds.get(tagKey);
					} catch (Exception e) {
		                tagEntity = new Entity("tagEntity", tag2mod);					//タグ名が新規のとき
		                tagKey = ds.put(tagEntity);
					}
	                
	                
	           	
	                txn.commit();														//ここでCommit

	                // Break out of retry loop.
	                success = true;

	            } catch (DatastoreFailureException e) {
	                // Allow retry to occur.
	            }
	            finally{
	            	if(txn.isActive())													//失敗していたらRollBack
	            		txn.rollback();
	            }
	        }
	        retries = 3;																//3回までRetry
	        success = false;
	    	//TagBelongs
	        while (!success && retries > 0) {
	            --retries;
	            Transaction txn=null;
	            try {
	           	
	                txn = ds.beginTransaction();										//Transaction開始
	                Entity tagBelong=null;
	            	try{
		            	tagBelong = new Entity("TagBelongs");							//タグとURLの紐づけ
		            	tagBelong.setProperty("tagKey", tagKey);
		            	tagBelong.setProperty("urlKey", urlKey);
	            	}
	            	catch(DatastoreFailureException e) {
	            		
	            	}
	            	ds.put(tagBelong);
	                txn.commit();														//ここでCommit

	                // Break out of retry loop.
	                success = true;

	            } catch (DatastoreFailureException e) {
	                // Allow retry to occur.
	            }
	            finally{
	            	if(txn.isActive())													//失敗していたらRollBack
	            		txn.rollback();
	            }
	        }
    	}
    	
    	//再描画(＋削除)	
        String tag; 	//再描画用
    	try {
	        Query q;
        	
        	urlKey = KeyFactory.stringToKey(tmp);										//urlKeyをKey型に
	    	Entity SiteList = ds.get(urlKey);											//タイトルを取り出す

            q = new Query("TagBelongs");												//TagBelongsを引くQuery用意
            q.addFilter("urlKey",
                    Query.FilterOperator.EQUAL,
                    urlKey);
            pq2 = ds.prepare(q);
        	
            Title title = new Title();													//JSON用のPOJO用意
        	title.title =  (String)SiteList.getProperty("title");
        	title.tagArray = new ArrayList<Tags>();
            for (Entity y : pq2.asIterable()) {											//タグがあるだけループ
            	tagKey = (Key)y.getProperty("tagKey");
            	tag = tagKey.getName();
            	
   	        	
            	if(cmd!=null && cmd.equals("del") && tag2mod.equals(tag)){				//削除する場合
            		ds.delete(y.getKey());
            	}
            	else{
            		Tags t = new Tags();												//タグをJSONに登録
            		t.tag = tag;
                 	title.tagArray.add(t);
            	}
            }
            resp.getWriter().write(JSON.encode(title));									//JSONエンコード
            
        	
		} catch (Exception e) {
			String message = e.toString();
		
		}
        

    }	
	
}
