(ns rpn.test.numbers
  (:use [rpn.numbers]
        [clojure.test]))

(defn disconnect [f]
  (binding [rpn.stack/pushf identity]
    (f)))

(use-fixtures :once disconnect)


(deftest test-num?
         (are [x] (num? x)
              "1" "1.0" ".5" "5.")
         (are [x] (not (num? x))
              "apple" "f" "1.1." "1..0" "1.0."))

(deftest test-hex?
         (are [x] (hex? x)
              "a" "A" "0A" "deadbeef")
         (are [x] (not (hex? x))
              "3g" "waffle" "2.0"))

(deftest test-process-num
         (are [f n] (= (process-num f) n)
              "3" 3.0
              "1.5" 1.5
              ".5" 0.5))

(deftest test-as-hex
         (are [x f] (= (as-hex x) f)
              "F" 15.
              "FF" 255.
              "f" 15.
              "ff" 255.))
