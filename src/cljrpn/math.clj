(ns cljrpn.math
  "Provides math functions that could not be found in standard libraries"
  (require [clojure.math.numeric-tower :as math]))

(defn- lax-integer? [i]
  "Helper method for factorial. Checks to see whether _i_ is an integer."
  (== i (Math/floor i)))

(defn factorial [n]
  ;we require that _n_ is a non-negative integer
  {:pre [(lax-integer? n) (not (neg? n))]}
  "Return the factorial of n"
  (loop [n n
         acc 1]
    (if (<= n 1)
      acc
      (recur (dec n) (* acc n)))))

(defn mean [& args]
  "Calculated the arithmetic mean of _args"
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + args) n))))

(defn variance [& args]
  "Computes the variance of _args_"
  (let [n (count args)]
    (if (pos? n)
      (/ (apply + (map #(* % %) args)) n))))

(defn stddev [& args]
  "Returns the standard deviation of _args_"
  (if-let [s2 (apply variance args)]
    (math/sqrt s2)))


(defn binom [m n]
  "Returns the binomial coefficent of (m n). Also known as \"m C n\" or \"m choose n\""
  (let [m-n (- m n)
        greater (max n m-n)
        lesser (max (min n m-n) 1)]
    (/ (loop [m m
              acc 1]
         (if (> m greater)
           (recur (dec m) (* acc m))
           acc)) (factorial lesser))))
