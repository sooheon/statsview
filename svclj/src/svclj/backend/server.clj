(ns svclj.backend.server
  (:require
   [clojure.java.io :as io]
   [mount.core :as mount]
   [reitit.ring :as ring]
   [ring.util.response :refer [resource-response content-type]]
   [reitit.dev.pretty :as pretty]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.coercion :as coercion]
   [reitit.swagger :as swagger]
   [ring.adapter.jetty :as jetty]
   [taoensso.nippy :as nippy]
   [muuntaja.core :as m]))

(def rushpassrec
  (->> (nippy/thaw-from-file "resources/rushpassrec.nippy")
       (filter #(> (:season %) 2010))))

(def handler
  (ring/ring-handler
   (ring/router
    [["/" {:get (fn [_] (some-> (resource-response "public/index.html")
                                (content-type "text/html; charset=utf-8")))}]
     ["/api"
      ["/stats"
       ["/nfl"
        ["/rushpassrec" {:get (fn [_]
                                {:status 200 :body rushpassrec})}]]]]]
    {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
     ;;:validate spec/validate ;; enable spec validation for route data
     ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
     :exception pretty/exception
     :data {:muuntaja m/instance
            :middleware [;; query-params & form-params
                         parameters/parameters-middleware
                         ;; content-negotiation
                         muuntaja/format-negotiate-middleware
                         ;; encoding response body
                         muuntaja/format-response-middleware
                         ;; exception handling
                         exception/exception-middleware
                         ;; decoding request body
                         muuntaja/format-request-middleware
                         ;; coercing response bodys
                         coercion/coerce-response-middleware
                         ;; coercing request parameters
                         coercion/coerce-request-middleware
                         ;; multipart
                         multipart/multipart-middleware]}})
   (ring/create-default-handler)))

(mount/defstate server
  :start
  (jetty/run-jetty #'handler {:port 8999 :join? false})
  :stop
  (.stop server))

(comment
 (mount/start)
 (mount/stop))
