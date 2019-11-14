(ns svclj.backend.core
  (:require
   [oz.core :as oz]
   [hiccup.table :as tbl]
   [semantic-csv.core :as sc]
   [semantic-csv.casters :as casters]
   [svclj.utils :as u]
   [svclj.data :as dat]))


(def contour-plot
  (oz/load "resources/contour_lines.json"))

(def viz
  [:div {:style {:font-family "HelveticaNeue"
                 :table {:border-collapse "collapse"}
                 :td {:padding "12px 15px"
                      :border-bottom "1px solid #E1E1E1"}
                 :th {:padding "12px 15px"
                      :border-bottom "1px solid #E1E1E1"}}}
   [:div
    [:h1 {:style {:font-family "HelveticaNeue"}}
     "Play stats analyzer"]
    (tbl/to-table1d
     (->> (group-by :playerid dat/rushpassrec)
          vals
          (map #(u/merge-stats dat/stat-cols %))
          (sort-by :rectd)
          reverse
          (take 10))
     (map vector
          (keys (first dat/rushpassrec))
          (keys (first dat/rushpassrec)))
     {:thead-attrs {:align "left"}
      :tbody-attrs {:align "left"}})]
   [:div
    [:vega-lite dat/line-plot]
    [:vega-lite dat/stacked-bar]
    [:vega contour-plot]]])


(comment
 (oz/start-server!)
 (oz/view! viz)
 #_(oz/build!
    [{:from "src/static_site/test.md"
      :to "resources/public/test.html"}]))
