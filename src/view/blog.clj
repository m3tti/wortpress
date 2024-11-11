(ns view.blog
  (:require
   [gaka.core :as gaka]
   [database.setting :as setting]
   [hiccup2.core :as h]
   [database.post :as post]
   [view.layout :as l]))

(defn show
  [req]
  (let [post (post/by-id (Integer. (get-in req [:params :id])))]
    (l/layout
     req
     [:div.container
      [:h1 (:posts/title post)]
      [:div (h/raw (:posts/content post))]])))

(defn- blog-entry
  [entry]
  (let [setting (setting/get-setting)]
    [:div.p-3.mb-3 {:style (gaka/inline-css
                                 :background (:settings/primary_color setting)
                                 :color (:settings/secondary_color setting)
                                 :border-color (:settings/secondary_color setting))}
     [:div
      [:a.text-decoration-none
       {:href (str "/blog/" (:posts/id entry))
        :style (gaka/inline-css
                :background (:settings/primary_color setting)
                :color (:settings/secondary_color setting))}
       [:div.fs-3 (:posts/title entry)]
       [:div.fs-6 (:posts/created_at entry)]]]]))

(defn index
  [req]
  (let [page (Integer. (get-in req [:params "page"] 1))
        elements (post/all-paged "" page)]
    (l/layout
     req
     
     (if (seq elements)
       [:div.container
        (map blog-entry elements)
        [:div.mt-4
         (l/paginator req page (int (Math/ceil (/ (post/item-count) 25))) "/blog")]]
       [:div.container
        [:h1.text-center "Nothing here yet!"]]))))
