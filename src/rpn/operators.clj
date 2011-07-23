(ns rpn.operators
  (:use [rpn.stack :only [apply-op]])
  (:require [clojure.contrib.math :as math]))

(defn- get-arity [op]
  (let [ometa (meta op)]
    (first (sort (:inline-arities ometa)))))

(defn- effect [before after]
  (str "[... " before " ] -> [... " after " ]"))

(defn build-op
  ([kwargs op help & arity]
   (let [arity (or (first arity) (get-arity op))
         kwargs (if (vector? kwargs) kwargs (vector kwargs))]
     (mapcat #(build-op [% op help arity] kwargs) kwargs)))
  ([[kw op help arity] aliases]
   (list (keyword kw) { :op op,
                       :help (apply effect help),
                       :arity arity
                       :cmds aliases}))

  ([args]
   (apply build-op args)))

(defn construct-ops [& ops]
  (apply hash-map
         (mapcat build-op ops)))


(def *operators*
  (construct-ops
    [:+ + ["x y" "x + y"]]
    [:- - ["x y" "x - y"] 2]
    [:* * ["x y" "x * y"]]
    [:neg #(- %) ["x" "-x"] 1]
    [["^" "**"] math/expt ["x y" "x**y"] 2]))
                
(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
