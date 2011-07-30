(ns cljrpn.math
  "Provides math functions that could not be found in standard libraries"
  (require [clojure.contrib.math :as math]))

(defn factorial [n]
  "Return the factorial of n"
  (loop [n n
         acc 1]
    (if (<= n 1)
      acc
      (recur (dec n) (* acc n)))))

(defn mean [& args]
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + args) n))))

(defn variance [& args]
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + (map #(* % %) args)) n))))

(defn stddev [& args]
  (if-let [s2 (apply variance args)]
    (math/sqrt s2)))
