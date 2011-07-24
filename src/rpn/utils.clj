(ns rpn.utils)

(defn as-vec [x]
  (cond
    (vector? x) x
    (list? x) (vec x)
    true (vector x)))

(defmacro construct [builder & specs]
  `(apply hash-map
    (mapcat #(apply ~builder %) (list ~@specs))))

(defn effect [before after]
  (str "(STACK EFFECT) [... " before " ] -> [... " after " ]"))

(defmacro maybe-let [tst bindings & body]
  (let [new-bindings
        (mapcat (fn [[v t f]]
                  [v (list `if tst t f)]) bindings)]
    `(let ~(vec new-bindings) ~@body)))
