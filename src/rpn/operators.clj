(ns rpn.operators
  (:use [rpn.stack :only [apply-op]])
  (:require [clojure.contrib.math :as math]))

(defn op-n
  ([op n kw]
   (list (keyword kw) {:op op :arity n}))
  ([op n]
   (let [ometa (meta op)
         kw (keyword (:name ometa))]
     (op-n op n kw))))

(defmulti build-op vector?)
(defmethod build-op true [args]
  (apply op-n args))
(defmethod build-op false [op]
  (op-n op 2))


(defn construct-ops [& ops]
  (apply hash-map
         (mapcat build-op ops)))

(def *operators*
  (ref
    (construct-ops
      + - * /
      [- 1 :neg]      [math/expt 2 "^"]
      [math/abs 1]
      [math/sqrt 1 "sqrt"])))
                
(defn operator? [o]
  (contains? @*operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (@*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
