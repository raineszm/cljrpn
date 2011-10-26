(ns cljrpn.operators
  "Implements the operations of the calculator."
  (:use [cljrpn.stack :only [apply-op]]
        [cljrpn.utils]
        [cljrpn.math])
  (:use [clojure.string :only [join]])
  (:require [clojure.math.numeric-tower :as math]))

(defn- get-arity [op]
  "A hackish way of getting a default arity for an operator."
  (let [ometa (meta op)]
    (first (sort (:inline-arities ometa)))))

(defn build-op 
  "Parses vector DSL provided to produce entrys for the *operators* map"
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
  "Generate a help string for a method provided via java interop"
  (let [args (map #(str "x" %) (range arity))]
    [(join " " args) (str name "(" (join ", " args) ")")]))

(defmacro java-math [name & [a & more :as other]]
  "Produce a DSL vector entry for *operators* using a method from the 
  java Math class"
  (maybe-let (number? a)
            [[arity  a 1]
             [more more other]]
            (let [strname (str name)
                  aliases (conj (vec more) (.toLowerCase strname))
                  args (repeatedly arity gensym)]
              `[~aliases (fn ~(vec args) (. Math ~name ~@args))
                ~(java-math-help strname arity) ~arity])))

(def
  ^:dynamic
  *operators*
  "A map of the available operators. Each entry is itself a map with entries:
  :op function corresponding the opreator
  :help the help text for the operator
  :arity the number of arguments required for the operator
  :cmds a list of inputs which correspond to this operator"
  (construct build-op
             [:+ + ["x y" "x + y"] 2]
             [:- - ["x y" "x - y"] 2]
             [:* * ["x y" "x * y"] 2]
             ["/" / ["x y" "x / y"] 2]
             [:neg #(- %) ["x" "-x"] 1]
             [["^" "**"] math/expt ["x y" "x**y"] 2]
             [["abs" "||"] math/abs ["x" "|x|"] 1]
             [["!" "fact"] factorial ["n" "n!"] 1]
             [["inv" "1/"] #(/ 1 %) ["x" "1/x"] 1]
             [["bin" "binom" "cmb"]
              binom
              (str "Calculates the binomial coefficent (m n) "
                   "also known as mCn or m choose n") 2]
             ["sum" + "Sums the contents of the stack" -1]
             ["prod" * "Multiplies the contents of the stack" -1]
             [["mean" "mn" "av" "average"] mean
               "Calculates the mean of all numbers on the stack" -1]
             ["var" variance
               "Calculates the variance of all numbers on the stack" -1]
             [["sd" "stddev" "std-dev"] stddev
               "Calculates the standard deviation of all numbers on the stack" -1]
             (java-math sqrt "v")
             (java-math sin "s")
             (java-math cos "c")
             (java-math tan "t")
             (java-math asin) 
             (java-math acos)
             (java-math atan)
             (java-math cosh "ch")
             (java-math sinh "sh")
             (java-math tanh "th")
             (java-math exp)
             (java-math log "ln")
             (java-math signum "sgn")
             (java-math toDegrees "deg")
             (java-math toRadians "rad")
             [:e #(Math/E) "Pushes the constant e to the stack" 1]
             [:pi #(Math/PI) "Pushes the constant pi to the stack" 1]))
                
(defn operator? [o]
  "Determine if the string o represents a valid operator"
  (contains? *operators* (keyword o)))

(defn process-op [o]
  "Apply the operator represented by o"
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if-not (apply-op op arity)
      (println "Too few numbers on stack for operator: " o))))
