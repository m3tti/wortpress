(ns config)

(def hotreload? false)

(def base-url "http://localhost:8080")

;; Postgress
(def db-opts
  {:dbtype "postgres"
   :dbname "wortpress"
   :user "postgres"
   :password "test1234"
   :port 15432})

;;(def db-opts
;;  {:dbtype "hsqldb"
;;   :dbname "./changeme"
   ;; set postgres dialect
;;   :sql.syntax_pgs true})

(def blog-name "WortPress")
