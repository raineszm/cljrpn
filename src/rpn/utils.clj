(ns rpn.utils)

(defn as-vec [x]
  (cond
    (vector? x) x
    (list? x) (vec x)
    true (vector x)))

(defmacro construct [builder & specs]
  `(apply hash-map
    (mapcat #(apply ~builder %) (list ~@specs))))
