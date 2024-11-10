(ns view.post
  (:require
   [database.post :as post]
   [taoensso.timbre :as log]
   [view.core :as c]
   [view.layout :as l]
   [utils.crud :as crud]
   [utils.database :as du])
  (:import
   (java.time LocalDateTime)))

(defn delete
  [req]
  (crud/delete!
   :req req
   :delete-fn post/delete!
   :redirect-path "/post"))

(defn normalize-data
  [req]
  (->
   (:params req)
   (du/parse-int "id")
   (assoc :created_at (LocalDateTime/now))
   du/remove-csrf
   du/remove-empty-vals
   du/->keywords))

(defn save
  [req]
  (log/debug req)
  (crud/upsert!
   :req req
   :create-fn post/insert!
   :update-fn post/update!
   :normalized-data (normalize-data req)
   :redirect-path "/post"))

(defn new
  [req]
  (l/layout
   req
   [:div.container
    (crud/create-update-form
     :save-path "/post"
     :form-inputs
     (map l/form-input
          [{:label "Title"
            :type "input"
            :name "title"}
           {:label "Content"
            :type "trix"
            :name "content"}]))]))

(defn edit
  [req]
  (let [post (post/by-id (Integer. (get-in req [:params :id])))]
    (l/layout
     req
     [:div.container
      (crud/create-update-form
       :save-path "/post"
       :form-inputs
       (map l/form-input
            [{:type "hidden"
              :name "id"
              :value (:posts/id post)}
             {:label "Title"
              :type "input"
              :name "title"
              :value (:posts/title post)}
             {:label "Content"
              :type "trix"
              :name "content"
              :value (:posts/content post)}]))])))

(defn index
  [req]
  (let [q (get-in req [:params "q"])
        page (Integer. (get-in req [:params "page"] "1"))]
    (l/layout
     req
     [:div 
      (crud/table-view
       :new-path "/post/new"
       :elements (post/all-paged q page)
       :edit-path-fn #(str "/post/" (:posts/id %) "/edit")
       :actions-fn
       (fn [e]
         [:div
          [:a.text-danger {:href "#"
                           :data-bs-toggle "modal"
                           :data-bs-target (str "#" (:posts/id e) "-delete")} "Delete"]
          (l/modal
           :id (str (:posts/id e) "-delete")
           :title "Delete Post"
           :content [:div "Delete Post really?"]
           :actions
           [:div
            [:button {:type "button" :class "btn btn-secondary" :data-bs-dismiss "modal"} "Close"]
            [:form.ms-2.d-inline {:action "/post/delete" :method "post"}
             (c/csrf-token)
             [:input {:type "hidden" :name "id" :value (:posts/id e)}]
             [:input.btn.btn-danger {:type "submit" :data-bs-dismiss "modal" :value "Delete"}]]]
)]))
      (l/paginator req page (post/item-count) "/post")])))
