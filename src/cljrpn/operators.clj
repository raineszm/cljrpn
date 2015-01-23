(ns cljrpn.operators
  "Implements the operations of the calculator."
  (:require
        [cljrpn.utils :refer [as-vec construct effect maybe-let]]
        [cljrpn.state :refer [popf]]
        [clojure.string :refer [join]]
        [cljrpn.math :refer :all]
        [clojure.math.numeric-tower :as math]))

(defn build-op [kwargs op help arity]
  "Parses vector DSL provided to produce entries for the op-table map.

  Our DSL is as follows. We use a 4 element vector to construct operators. It consists of:

  kwargs - a symbol/string or a vector of symbols/strings which give the tokens that will be used for this command in the program.
  op - a function implementing the operation. It takes __arity__ floating point numbers and returns a floating point number
  help - the help text for the operator
  arity - the number of arguments that this operator accepts. -1 indicates an unlimited number of arguments.

  Returns a list of vectors of the form [:kwarg operator-map]"
   (let [kwargs (as-vec kwargs)
         help (if (vector? help) (apply effect help) help)]
     (mapcat (fn [kw]
               [(keyword kw) { :op op,
                              :help help
                              :arity arity
                              :cmds kwargs}]) kwargs)))

(defn java-math-help [name arity]
  "Generate appropriate arguments for the help string builder for a method provided via java interop"
  (let [args (map #(str "x" %) (range arity))]
    [(join " " args) (str name "(" (join ", " args) ")")]))

(defmacro java-math [fn-name & [frst & rst :as more]]
  "Produce a DSL vector entry  as suitable for build-op for operators using a method from the java Math class"
  ;maybe-let is provided by cljrpn.utils
  (maybe-let (number? frst)
            [[arity
              frst 1]
             [more
              rst more]]
            (let [fn-name-str (str fn-name)
                  ;gather up the java name and any provided alternatives
                  aliases (conj (vec more) (.toLowerCase fn-name-str))
                  arg-names (repeatedly arity gensym)]
              ;create the DSL entry for __build-op__
              `[~aliases
                (fn ~(vec arg-names)
                  (. Math ~fn-name ~@arg-names))
                ~(java-math-help fn-name-str arity)
                ~arity])))

(def
  op-table
  "A map of the available operators of the form {..., :op-name => entry}. Each entry is itself a map with entries:
  :op function corresponding the operator
  :help the help text for the operator
  :arity the number of arguments required for the operator
  :cmds a list of inputs which correspond to this operator"
  ;construct is provided by cljrpn.utils
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
             [:e #(Math/E) "Pushes the constant e to the stack" 0]
             [:pi #(Math/PI) "Pushes the constant pi to the stack" 0]))

(defn operator? [o]
  "Determine if the string _o_ represents a valid operator"
  (contains? op-table (keyword o)))

(defn apply-op [state op n]
  "Apply the function _op_ to the top _n_ members of the stack. Returns the updated state."
  (let [stack (:stack state)]
    (if-let [new-stack
             (cond
               (< n 0)
                 (list (apply op (reverse stack)))
               (>= (count stack) n)
                 (let [args (take n stack)]
                   (cons (apply op (reverse args)) (drop n stack))))]
      (assoc state :stack new-stack))))

(defn process-op [state o]
  "Apply the operator represented by _o_. Returns the updated state."
  (let [{:keys [op arity]} (op-table (keyword o))]
    (try
      (if-let [state (apply-op state op arity)]
        state
        (println "Too few numbers on stack for operator: " o))
    (catch AssertionError e
      (println "Precondition for" o "not met.")))))
