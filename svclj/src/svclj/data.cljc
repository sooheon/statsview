(ns svclj.data
  #?(:clj (:require [semantic-csv.core :as sc]
                    [semantic-csv.casters :as casters]
                    [taoensso.nippy :as nippy]))
  #?(:cljs (:require [ajax.core :refer [GET POST]])))

(defn play-data [& names]
  (for [n names
        i (range 20)]
    {:time i
     :item n
     :quantity (+ (Math/pow (* i (count n)) 0.8)
                  (rand-int (count n)))}))

(def line-plot
  {:data {:values (play-data "monkey" "slipper" "broom")}
   :encoding {:x {:field "time"}
              :y {:field "quantity"}
              :color {:field "item" :type "nominal"}}
   :mark "line"})

(def stacked-bar
  {:data {:values (play-data "munchkin" "witch" "dog" "lion" "tiger" "bear")}
   :mark "bar"
   :encoding {:x {:field "time"
                  :type "ordinal"}
              :y {:aggregate "sum"
                  :field "quantity"
                  :type "quantitative"}
              :color {:field "item"
                      :type "nominal"}}})


#?(:clj (def stat-cols [:cpmp :ptd :rectar :rnet :recyds :ratt :rec :rectd
                        :rtd :pyds :pint :pint :patt :fid]))

(defn rushpassrec []
 #?(:clj (->> (sc/slurp-csv "resources/data/NFL/NFL_boxscore_rushpassrec.csv")
              (sc/cast-with casters/->int {:only (conj stat-cols :season)}))
    :cljs (GET "localhost:8999/api/stats/nfl/rushpassrec")))


(comment
 (nippy/freeze-to-file "resources/rushpassrec.nippy" (rushpassrec)))
