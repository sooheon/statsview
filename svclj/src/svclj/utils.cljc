(ns svclj.utils
  (:require [clojure.set :as set]))

(defn flatten-keys
  "Unpacks keys which are collections (i.e. [:foo :bar]) into individual map
   entry keys.

   {[:foo :bar] 1, :baz 2} => {:foo 1 :bar 1 :baz 2}"
  [m]
  (reduce (fn [result [k v]]
            (if (coll? k)
              (let [submap (into {} (map vector k (repeat v)))]
                (assert (empty? (set/intersection
                                 (set (keys submap))
                                 (set (keys m)))))
                (merge
                 (dissoc result k)
                 submap))
              result))
          m
          m))

(defn merge-by
  "Works like merge-with, but fns is a mapping of keys to merge-fn.

   Valid opts:
    - `:default` overrides the default strategy of taking the rightmost value.
    - `:key->fn` holds a mapping of keys to key-specific merge-fns. Each merge-fn
      has signature [lval rval] => resval.
    - Keys in `:ignore` are dissoc'ed from input maps.

  Map given to :key->fn is run through `flatten-keys`. This is to allow giving
  multiple keys that map to a single fn as a mapping of collection of keys to
  single fn."
  [{:keys [key->fn default ignore]
    :or {default (fn [_ x] x)
         key->fn {}
         ignore []}}
   & maps]
  (when (some identity maps)
    (let [key->fn (flatten-keys key->fn)
          merge-entry (fn [m [k v]]
                        (if (contains? m k)
                          (let [f (or (key->fn k) default)]
                            (assoc m k (f (get m k) v)))
                          (assoc m k v)))
          merge2 (fn [m1 m2]
                   (reduce merge-entry (or m1 {}) (seq m2)))]
      (reduce merge2 (map #(apply dissoc % ignore) maps)))))

(defn maybe-add [l r]
  (cond
    (and (nil? l) (nil? r)) nil
    (nil? l) r
    (nil? r) l
    :else (+ l r)))

(defn merge-stats [stat-cols entries]
  (apply
   merge-by
   {:key->fn {stat-cols maybe-add}}
   entries))
