(ns database.setting
  (:require
   [database.core :as db]))

(defn insert!
  [setting]
  (db/insert! :settings setting))

(defn update!
  [setting]
  (db/update! :settings setting {:id (get setting "id")}))

(defn get-setting
  []
  (db/execute-one! {:select :* :from :settings}))
