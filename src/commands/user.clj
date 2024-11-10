(ns commands.user
  (:require
   [database.user :as user]))

(defn create-admin
  [& args]
  (if (< (count args) 2)
    (println "<email> <password> needed!")
    (user/insert {:email (first args) :password (second args)})))
