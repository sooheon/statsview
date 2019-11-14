(ns ^:figwheel-hooks svclj.frontend.core
  (:require
   [goog.dom :as gdom]
   [svclj.frontend.views :as views]
   [reagent.core :as reagent :refer [atom]]))

(println "This text is printed from src/hello_world/core.cljs. Go ahead and edit it and see reloading in action.")


(defn app []
  [views/viz])

(defn mount! []
  (when-let [el (gdom/getElement "app")]
    (reagent/render-component [app] el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount!)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount!))
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
