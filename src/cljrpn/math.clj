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
  (loop [n n
         acc 1]
    (if (<= n 1)
      acc
      (recur (dec n) (* acc n)))))

(defn mean
  "Calculated the arithmetic mean of _args"
  [& args]
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + args) n))))

(defn variance
  "Computes the variance of _args_"
  [& args]
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + (map #(* % %) args)) n))))

(defn stddev
  "Returns the standard deviation of _args_"
  [& args]
  (if-let [s2 (apply variance args)]
    (math/sqrt s2)))


(defn binom
  "Returns the binomial coefficent of (m n). Also known as \"m C n\" or \"m choose n\""
  [m n]
  (let [m-n (- m n)
        greater (max n m-n)
        lesser (max (min n m-n) 1)]
    (/ (loop [m m
              acc 1]
         (if (> m greater)
           (recur (dec m) (* acc m))
           acc)) (factorial lesser))))
