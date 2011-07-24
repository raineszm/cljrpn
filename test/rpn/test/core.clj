(ns rpn.test.core
  (:use [rpn.core])
  (:use [clojure.test])
  (:use [clojure.contrib.str-utils :only [chomp]]))


(deftest integration
         (testing "Basic Arithmetic"
                  (are [line value]
                       (= (chomp (with-out-str (process-line line))) value)
                       "1 2 + ." "3.0"
                       "2 3 * ." "6.0"
                       "3 2 - ." "1.0"
                       "6 3 / ." "2.0"
                       "2 3 ^ ." "8.0")))
