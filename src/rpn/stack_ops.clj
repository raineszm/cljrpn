(ns rpn.stack-ops
  (:use [rpn.stack]))

(defn stack-pop []
  (if (stacked? 1)
    (println (popf))
    (println "The stack is empty...")))

(defn stack-show []
  (println "TOP> " @main-stack " <BOTTOM")) 

(def *stack-ops*
  {
   :. stack-pop
   :.s stack-show})

(defn stack-op? [o]
  (contains? *stack-ops* (keyword o)))

(defn process-stackop [o]
  ((*stack-ops* (keyword o))))

