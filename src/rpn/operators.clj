(ns rpn.operators
  (:use [rpn.stack :only [apply-op]]))

(def *operators* 
  {
   :+ {:op + :arity 2}
   :- {:op - :arity 2}
   :* {:op * :arity 2}
   (keyword "/") {:op / :arity 2}})

(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
