(ns database.post
  (:require
   [database.core :as db]))

(defn insert!
  [post]
  (db/insert! :posts post))

(defn delete!
  [id]
  (db/delete! :posts {:id id}))

(defn update!
  [post]
  (db/update! :posts post {:id (get post "id")}))

(defn find-by-keys
  [post]
  (db/find-by-keys :posts post))

(defn by-id
  [id]
  (first (db/find-by-keys :posts {:id id})))

(defn all-paged
  [q page]
  (db/execute!
   (db/paginate-and-search
    :table :posts
    :where (when q
             [:like :title (str "%" q "%")])
    :page page)))

(defn all
  []
  (db/execute! {:select :* :from :posts}))

(defn item-count
  []
  (db/item-count :posts))

(comment (insert! {:title "hans" :content "wurst"}))
(comment (all-paged "" 1))
