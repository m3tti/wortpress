(ns commands.user
  (:require
   [database.user :as user]))

(defn create-admin
  [& args]
  (println "Password:")
  (println (read)))
