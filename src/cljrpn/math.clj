(ns cljrpn.math
  "Provides math functions that could not be found in standard libraries"
  (:require [clojure.math.numeric-tower :as math]))

(defn- lax-integer?
  "Helper method for factorial. Checks to see whether _i_ is an integer."
  [i]
  (== i (Math/floor i)))

(defn factorial
  ;we require that _n_ is a non-negative integer
  "Return the factorial of n"
  [n]
  {:pre [(lax-integer? n) (not (neg? n))]}
  (reduce * (range 1 (inc n))))

(defn mean
  "Calculated the arithmetic mean of _args_"
  [& args]
  (let [n (count args)]
    (when (pos? n)
      (/ (apply + args) n))))

(defn variance
  "Computes the variance of _args_"
  [& args]
  (let [n (count args)]
    (when (pos? n)
      (/ (apply + (map #(* % %) args)) n))))

(defn stddev
  "Returns the standard deviation of _args_"
  [& args]
  (when-let [s2 (apply variance args)]
    (math/sqrt s2)))

(defn binom
  "Returns the binomial coefficent of (m n). Also known as \"m C n\" or \"m choose n\""
  [m n]
  (let [m-n (- m n)
        greater (max n m-n)
        lesser (max (min n m-n) 1)]
    (/ (reduce * (range (inc greater) (inc m)))
       (factorial lesser))))
