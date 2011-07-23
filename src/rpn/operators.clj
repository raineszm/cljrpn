(ns rpn.operators
  (:use [rpn.stack :only [apply-op]]
        [rpn.math])
  (:use [clojure.string :only [join]])
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
   (let [help (if (vector? help) (apply effect help) help)]
     (list (keyword kw) { :op op,
                         :help help,
                         :arity arity
                         :cmds aliases})))
  ([args]
   (apply build-op args)))

(defn construct-ops [& ops]
  (apply hash-map
         (mapcat build-op ops)))

(defn java-math-help [name arity]
  (let [args (map #(str "x" %) (range arity))]
    [(join " " args) (str name "(" (join ", " args) ")")]))

;TODO:This still needs work
(defmacro java-math [name & aliases]
  (let [strname (str name)
        aliases (conj (vec aliases) strname)
        arity 1]
    `[~aliases #(. Math ~name %)
      ~(java-math-help strname arity) ~arity]))


(def *operators*
  (construct-ops
    [:+ + ["x y" "x + y"]]
    [:- - ["x y" "x - y"] 2]
    [:* * ["x y" "x * y"]]
    ["/" / ["x y" "x / y"]]
    [:neg #(- %) ["x" "-x"] 1]
    [["^" "**"] math/expt ["x y" "x**y"] 2]
    [["!" "fact"] factorial ["n" "n!"] 1]
    ["sum" + "Sums the contents of the stack" -1]
    ["prod" * "Multiplies the contents of the stack" -1]
    (java-math sqrt "v")
    (java-math sin "s")
    (java-math cos "c")
    (java-math tan "t")
    (java-math asin)
    (java-math acos)))
                
(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
