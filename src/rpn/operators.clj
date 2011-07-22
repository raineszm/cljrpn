(ns rpn.operators
  (:use [rpn.stack :only [apply-op]]))

(defn binary-op
  ([op arity]
   (let [ometa (meta op)
         kw (keyword (:name ometa))
         arity (or arity (first (sort (:inline-arities ometa))))]
     [kw {:op op :arity arity}]))
  ([op]
   (if (vector? op)
     (apply binary-op op)
     (binary-op op nil))))

(defn construct-ops [& ops]
  (apply hash-map
         (mapcat binary-op ops)))


;TODO: Fix arity on -
(def *operators* (construct-ops + [- 2] * /))
                
(defn operator? [o]
  (contains? *operators* (keyword o)))

(defn process-op [o]
  (let [{:keys [op arity]} (*operators* (keyword o))]
    (if (apply-op op arity)
      true
      (println "Too few numbers on stack for operator: " o))))
