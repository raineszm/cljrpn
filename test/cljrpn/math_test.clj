(ns cljrpn.math-test
  (:require [cljrpn.math :as m]
            [clojure.test :as t]))

(t/deftest factorial
  (t/testing "Basic factorial behavior"
    (t/are [n value] (= (m/factorial n) value)
      0      1
      1      1
      2      2
      3      6
      4      24
      5      120
      6      720))
  (t/testing "Disallowed factorial arguments"
    (t/are [n] (thrown? AssertionError (m/factorial n))
      -1 4.2 -4.2)))

(t/deftest mean
  (t/is (= (m/mean 1 2 3 4 5) 3)))

(t/deftest variance
  (t/is (= (m/variance 1 2 3 4 5) 11)))

(t/deftest stddev
  (t/is (= (m/stddev 1 2 3 4 5) (Math/sqrt 11.0))))
