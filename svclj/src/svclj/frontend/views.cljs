(ns svclj.frontend.views
  (:require [svclj.data :as dat]
            [oz.core :as oz]
            [ajax.core :as ajax]))

(defn viz []
  [:div
   [:div
    [:h2 "Play stats analyzer!"]]
   [:pre (str (js->clj (ajax/GET "http://localhost:8999/api/stats/nfl/rushpassrec")))]
   [:div
    [oz/vega-lite dat/line-plot]
    [oz/vega-lite dat/stacked-bar]]])
