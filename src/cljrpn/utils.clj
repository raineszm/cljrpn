(ns cljrpn.utils
  "Help functions and macros used to simplify the code of this project")

(defn as-vec
  "Converts x to a vector by introducing the minimally needed degree of nesting."
  [x]
  (cond
    (vector? x) x
    (list? x) (vec x)
    :else (vector x)))

(defmacro construct
  "Used to build a map of data specified as a list of vectors. builder is applied to each spec vector in order to produce the resultant entries."
  [builder & specs]
  `(apply hash-map
    (mapcat #(apply ~builder %) (list ~@specs))))

(defn effect
  "Formats a stack effect for an operation"
  [before after]
  (str "(STACK EFFECT) [... " before " ] -> [... " after " ]"))

(defmacro maybe-let
  "Bindings is a vector of vectors of the form [v t f] where
  v is the symbol to be bound,
  t is the true value,
  and f the false value.
  Produces a let block where each symbol will be bound to its true value
  if tst is true at execution time and else be bound to the false value."
  [tst bindings & body]
  (let [new-bindings
        (mapcat (fn [[v t f]]
                  [v (list 'if tst t f)]) bindings)]
    `(let ~(vec new-bindings) ~@body)))
