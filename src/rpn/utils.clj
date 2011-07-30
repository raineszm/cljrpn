(ns rpn.utils)

(defn as-vec [x]
  "Converts x to a vector by introducing the minimally needed degree of nesting."
  (cond
    (vector? x) x
    (list? x) (vec x)
    true (vector x)))

(defmacro construct [builder & specs]
  "Used to build a map of data specified as a list of vectors. builder is applied
  to each spec vector in order to produce the resultant entries."
  `(apply hash-map
    (mapcat #(apply ~builder %) (list ~@specs))))

(defn effect [before after]
  "Formats a stack effect for an operation"
  (str "(STACK EFFECT) [... " before " ] -> [... " after " ]"))

(defmacro maybe-let [tst bindings & body]
  "Bindings is a vector of vectors of the form [v t f] where
  v is the symbol to be bound t is the true value and f the false value.
  Produces a let block where each symbol will be bound to it's true value
  if tst is true at execution time and else be bound to the false value."
  (let [new-bindings
        (mapcat (fn [[v t f]]
                  [v (list 'if tst t f)]) bindings)]
    `(let ~(vec new-bindings) ~@body)))
