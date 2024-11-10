(ns view.setting
  (:require
   [taoensso.timbre :as log]
   [view.core :as c]
   [database.setting :as setting]
   [utils.response :as r]
   [utils.database :as du]
   [utils.error :as e]
   [view.layout :as l]))

(defn normalize
  [req]
  (->
   (:params req)
   du/remove-csrf
   du/remove-empty-vals
   (du/parse-int "id")))

(defn save
  [req]
  (try
    (if (empty? (get-in req [:params "id"]))
      (setting/insert! (normalize req))
      (setting/update! (normalize req)))
    (catch Exception e
      (log/error e)
      (r/flash-msg (r/redirect "/setting")
                   "danger" (e/stacktrace->str e))))
  (r/redirect "/setting"))

(defn index
  [req]
  (let [setting (setting/get-setting)]
    (l/layout
     req
     [:div.container
      [:form {:action "/setting" :method "post"}
       (c/csrf-token)
       [:input {:type "hidden" :name "id" :value (:settings/id setting)}]
       [:div.mb-3
        [:label.form-label "Primary Color"]
        [:input.form-control {:type "color" :name "primary_color" :value (:settings/primary_color setting)}]]
       [:div.mb-3
        [:label.form-label "Secondary Color"]
        [:input.form-control {:type "color" :name "secondary_color" :value (:settings/secondary_color setting)}]]
       [:div.mb-3
        [:label.form-label "Font"]
        [:input.form-control {:type "input" :name "font" :value (:settings/font setting)}]]
       [:div.d-grid
        [:input.btn.btn-primary {:type "submit" :value "Save"}]]]])))

(setting/get-setting)
