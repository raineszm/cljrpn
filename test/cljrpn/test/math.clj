(ns cljrpn.test.math
  (:use [cljrpn.math]
        [clojure.test]))

(deftest test-factorial
         (are [n value]
              (= (factorial n) value)
              0 1
              1 1
              2 2
              3 6
              4 24
              5 120
              6 720)
         (are [n]
              -1
              4.2
              -4.2
           (thrown? AssertionError (factorial n))))

(deftest test-mean
         (is (=
               (mean 1 2 3 4 5)
               3)))

(deftest test-variance
          (is (=
                (variance 1 2 3 4 5)
                11)))

(deftest test-stddev
         (is (=
               (stddev 1 2 3 4 5)
               (Math/sqrt 11.0))))



