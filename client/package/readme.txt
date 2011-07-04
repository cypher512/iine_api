・インストール
このディレクトリの中身を全て、Web Serverのしかるべきフォルダにコピーしてください

・構成
1. Json形式のテキストファイルを用意                 url_list.txt
2. 1.のファイルをAjaxで読み込むjavascriptを用意     Silverlight.js
3-1. 2のjavascriptをSilverlight(C#)からcall         iine_sample.xap
3-2. 取り出したJsonをSilverlightへcall back         〃
4. JsonをバラしてSilverlightのHyperlinkButtonに代入 〃
5. Silverlightを読み込むhtml                        index.html

・サーバ環境の設定
拡張子xapのAddTypeが必要です
AddType application/x-silverlight-app xap

