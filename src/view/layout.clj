(ns view.layout
  (:require
   [config :refer [hotreload?]]
   [gaka.core :as gaka]
   [cheshire.core :as json]
   [hiccup2.core :as h]
   [utils.response :as r]
   [utils.session :as s]
   [database.setting :as setting]
   [utils.htmc :as hc]
   [view.style :as sty]
   [view.core :as c]))


(def squint-cdn-path "https://cdn.jsdelivr.net/npm/squint-cljs@0.8.114")

(defn paginator [req current-page pages base-url]
  (let* [setting (setting/get-setting)
         q (:query-params req)
         next (when (not= current-page pages)
                (str base-url "?"
                     (r/query-params->url
                      (merge q {"page" (+ current-page 1)}))))
         previous (when (not= current-page 1)
                    (str base-url "?"
                         (r/query-params->url
                          (merge q {"page" (- current-page 1)}))))]
    [:div.d-flex.justify-content-center.mb-2
     [:div.btn-group
      [:a.btn.btn-outline-secondary
       (merge {:style (gaka/inline-css :border-color (:settings/secondary_color setting)
                                       :color (:settings/secondary_color setting))}
              (if (nil? previous)
                {:disabled true}
                {:href previous})) "Previous"]
      [:a.btn.btn-outline-secondary
       {:href "#"
        :style (gaka/inline-css :border-color (:settings/secondary_color setting)
                                :color (:settings/secondary_color setting))}
       current-page " / " pages ]
      [:a.btn.btn-outline-secondary
       (merge {:style (gaka/inline-css :border-color (:settings/secondary_color setting)
                                       :color (:settings/secondary_color setting))}
              (if (nil? next)
                {:disabled true}
                {:href next})) "Next"]]]))

(defn autocomplete-input [& {:keys [label name value list required]}]
  [:div.mb-3
   [:label.form-label label]
   [:input.form-control {:type "input" :list (str name "list")
            :name name :value value :required required
            :autocomplete "off"}]
   [:datalist {:id (str name "list")}
    (map (fn [e] [:option {:value e}]) list)]])

(defn form-input [& {:keys [label type name value required]
                     :as opts
                     :or {required false}}]
  (cond
    (= type "textarea")
    [:div.mb-3
     [:label.form-label label]
     [:textarea.form-control {:type type :name name :required required} value]]

    (= type "autocomplete")
    (autocomplete-input opts)

    (= type "trix")
    [:div.mb-3
     [:label.form-label label]
     [:input {:id label :type "hidden" :name name :value value}]
     [:trix-editor {:input label}]]
    
    :else
    (if label 
      [:div.mb-3
       [:label.form-label label]
       [:input.form-control {:type type :value value :name name :required required}]]

      [:input.form-control {:type type :value value :name name :required required}])))

;;
;; Extend importmap. This enables you to load other libraries in your
;; js files. The key is the libraries name in your app if you require it
;;
(defn global-importmap []
  [:script {:type "importmap"}
   (h/raw
    (json/encode 
     {:imports
      {:squint-cljs/core.js (str squint-cdn-path "/src/squint/core.js")
       :squint-cljs/string.js (str squint-cdn-path "/src/squint/string.js")
       :squint-cljs/src/squint/string.js (str squint-cdn-path "/src/squint/string.js")
       :squint-cljs/src/squint/set.js (str squint-cdn-path "/src/squint/set.js")
       :squint-cljs/src/squint/html.js (str squint-cdn-path "/src/squint/html.js")}}))])

(defn navbar [req]
  (let [user (s/current-user req)
        setting (setting/get-setting)]
    [:nav.navbar.sticky-top.navbar-expand-lg.navbar-bg-body-tertiary
     [:div.container
      [:a.navbar-brand.fw-bold
       {:href "/"
        :style (gaka/inline-css :color (:settings/secondary_color setting))} config/blog-name]
      [:button.navbar-toggler {:type "button" :data-bs-toggle "collapse" :data-bs-target "#navbar"}
       [:span.navbar-toggler-icon]]
      [:div#navbar.collapse.navbar-collapse
       (when (not user)
         [:ul.navbar-nav
          
          ])
       (when user
         [:ul.navbar-nav
          [:li.nav-item
           [:a.nav-link {:href "/post"
                         :style (gaka/inline-css :color (:settings/secondary_color setting))} "Posts"]]
          [:li.nav-item
           [:a.nav-link {:href "/setting"
                         :style (gaka/inline-css :color (:settings/secondary_color setting))} "Settings"]]
          [:li.nav-item
           [:a.nav-link {:href "/logout"
                         :style (gaka/inline-css :color (:settings/secondary_color setting))} "Logout"]]])]]]))

(defn alert [req]
  (let* [msg (get-in req [:flash :message])
         severity (:severity msg)
         msg (:message msg)]
    [:div.alert {:class (str "alert-" severity) :role "alert"}
     msg]))

(defn layout [req & body]
  (str
   (h/html
    [:html
     (let [settings (database.setting/get-setting)]
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport"
                :content "width=device-width, initial-scale=1"}]
        [:link {:rel "manifest" :href "/manifest.json"}]
        [:link {:href "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
                :rel "stylesheet"
                :integrity "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
                :crossorigin "anonymous"}]
        [:link {:rel "stylesheet"
                :type "text/css"
                :href "https://unpkg.com/trix@2.0.8/dist/trix.css"}]
        [:script {:type "text/javascript"
                  :src "https://unpkg.com/trix@2.0.8/dist/trix.umd.min.js"}]
        (global-importmap)
        (c/cljs-module "register-sw")
        (when hotreload?
          (c/cljs-module "hotreload"))
        [:style (h/raw sty/*style*)]         
        [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
        [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
        [:link {:href (str "https://fonts.googleapis.com/css2?family=" (:settings/font settings) ":wght@100..900&display=swap") :rel "stylesheet"}]
        [:style (h/raw (gaka.core/css [:body
                                       :background-color (:settings/primary_color settings)
                                       :font-family (str "'" (:settings/font settings) "', sans-serif")
                                       :color (:settings/secondary_color settings)]))]])
        [:body {:data-bs-theme "dark" :id "body"}
         (hc/htmc)
         (navbar req)
         (alert req)
         body                 
         [:script {:src "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                   :integrity "sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
                   :crossorigin "anonymous"}]]])))

(defn modal [& {:keys [id title content actions]}]
  [:div.modal.fade {:tabindex -1 :id id}
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header
      [:h5.modal-title title]
      [:button.btn-close {:type "button" :data-bs-dismiss "modal"}]]
     [:div.modal-body content]
     [:div.modal-footer actions]]]])
