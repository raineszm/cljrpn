(ns rpn.operators
  (:use [rpn.stack :only [apply-op]])
  (:require [clojure.contrib.math :as math]))

(defn- get-arity [op]
  (let [ometa (meta op)]
    (first (sort (:inline-arities ometa)))))

(defn build-op
  ([kwargs op help & arity]
   (let [arity (or (first arity) (get-arity op))]
     (if (vector? kwargs)
       (mapcat #(build-op % op help arity) kwargs)
       (list (keyword kwargs) { :op op,
                               :help help,
                               :arity arity}))))
  ([args]
   (apply build-op args)))

(defn construct-ops [& ops]
  (apply hash-map
         (mapcat build-op ops)))

(defn- effect [before after]
  (str "[... " before " ] -> [... " after " ]"))

(def *operators*
  (construct-ops
    [:+ + (effect "x y" "x + y")]
    [:- - (effect "x y" "x - y") 2]
    [:* * (effect "x y" "x * y")]
    [:neg #(- %) (effect "x" "-x") 1]
    [["^" "**"] math/expt (effect "x y" "x**y") 2]))
                
(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
