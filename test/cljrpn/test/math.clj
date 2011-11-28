(ns cljrpn.test.math
  (:use cljrpn.math
        midje.sweet
        clojure.test))

(tabular
  (fact "Basic factorial behavior"
        (factorial ?n) => ?value)
  ?n    ?value
  0      1
  1      1
  2      2
  3      6
  4      24
  5      120
  6      720)

(tabular
  (fact "Disallowed factorial arguments"
        (factorial ?n) => (throws AssertionError))
  ?n -1 4.2 -4.2)

(fact (mean 1 2 3 4 5) => 3)

(fact (variance 1 2 3 4 5) => 11)

(fact (stddev 1 2 3 4 5) => (Math/sqrt 11.0))
