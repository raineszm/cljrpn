(ns rpn.operators
  (:use [rpn.stack :only [apply-op]]
        [rpn.utils]
        [rpn.math])
  (:use [clojure.string :only [join]])
  (:require [clojure.contrib.math :as math]))

(defn- get-arity [op]
  (let [ometa (meta op)]
    (first (sort (:inline-arities ometa)))))

(defn build-op 
  ([kwargs op help arity]
   (let [kwargs (as-vec kwargs)
         help (if (vector? help) (apply effect help) help)]
     (mapcat (fn [kw]
               [(keyword kw) { :op op,
                              :help help
                              :arity arity
                              :cmds kwargs}]) kwargs)))
  ([kwargs op help]
   (build-op kwargs op help (get-arity op))))

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
  (construct build-op
    [:+ + ["x y" "x + y"]]
    [:- - ["x y" "x - y"] 2]
    [:* * ["x y" "x * y"]]
    ["/" / ["x y" "x / y"]]
    [:neg #(- %) ["x" "-x"] 1]
    [["^" "**"] math/expt ["x y" "x**y"] 2]
    [["abs" "||"] math/abs ["x" "|x|"] 1]
    [["!" "fact"] factorial ["n" "n!"] 1]
    [["inv" "1/"] #(/ 1 %) ["x" "1/x"] 1]
    ["sum" + "Sums the contents of the stack" -1]
    ["prod" * "Multiplies the contents of the stack" -1]
    (java-math sqrt "v")
    (java-math sin "s")
    (java-math cos "c")
    (java-math tan "t")
    (java-math asin)
    (java-math acos)
    [:e #(Math/E) "Pushes the constant e to the stack" 1]
    [:pi #(Math/PI) "Pushes the constant pi to the stack" 1]))
                
(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
