(ns routes
  (:require
   [ruuter.core :as ruuter]
   [static :as static]
   [utils.hotreload :as hotreload]
   [taoensso.timbre :as log]
   [view.post :as post]
   [view.blog :as blog]
   [view.setting :as setting]
   [view.feed :as feed]
   [view.pwa :as pwa]
   [view.login :as login]))

(defn route [path method response-fn]
  {:path path
   :method method
   :response (fn [req]
               ;;(log/debug req)
               (let [resp (response-fn req)]
                 (if (string? resp)
                   {:status 200
                    :body resp}
                   resp)))})

(defn get [path response-fn]
  (route path :get response-fn))

(defn post [path response-fn]
  (route path :post response-fn))

(defn put [path response-fn]
  (route path :put response-fn))

(defn delete [path response-fn]
  (route path :delete response-fn))

(defn option [path response-fn]
  (route path :option response-fn))

;;
;; Extend your routes in here!!!
;;
(def routes
  #(ruuter/route 
    [(get "/manifest.json" pwa/manifest)
     (get "/sw.js" pwa/sw)
     (get "/static/:filename" static/serve-static)
     (get "/" blog/index)     
     (get "/login" login/index)
     (post "/login" login/login)
     (get "/logout" login/logout)
     (get "/hotreload" hotreload/hotreload)
     ;; My routes
     (get "/post" post/index)
     (post "/post" post/save)
     (get "/post/new" post/new)
     (get "/post/:id/edit" post/edit)
     (post "/post/delete" post/delete)
     ;; Blog
     (get "/blog" blog/index)
     (get "/blog/:id" blog/show)
     ;; Setting
     (get "/setting" setting/index)
     (post "/setting" setting/save)
     ;; Feed
     (get "/feed" feed/feed)]
    %))
