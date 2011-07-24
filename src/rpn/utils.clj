(ns rpn.utils)

(defn as-vec [x]
  (cond
    (vector? x) x
    (list? x) (vec x)
    true (vector x)))
