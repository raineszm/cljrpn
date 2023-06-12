(ns cljrpn.numbers-test
  (:require [cljrpn.numbers :as n]
            [cljrpn.state :as s]
            [cljrpn.test-helpers :as th]
            [clojure.test :as t]))

(t/deftest decimal-matching
  (t/testing "The finer points of number matching"
    (t/are  [number] (n/num? number)
      "1" "1.0" ".5" "5." "-4" "-4.3" "-.5" "1e0"
      "1e5" "1e-5" "-3e2" "-4e-3" "0.5e2" "1.6e17"))

  (t/testing "The finer points of number matching (unwanted matches)"
    (t/are [number] (not (n/num? number))
      "apple" "f" "1.1." "1..0" "1.0."
      "." "-." ".e4")))

(t/deftest hexidecimal
  (t/are  [val f] (f (n/hex? val))
    "a"          identity
    "A"          identity
    "0A"         identity
    "deadbeef"   identity
    "3g"         not
    "waffle"     not
    "2.0"        not))

(t/deftest process-num "Numbers are added to the stack"
  (t/are  [str num]
          (th/stack=
           (n/process-num (s/new-state '(1)) str)
           [num 1])
    "2.5" 2.5
    "3"   3.0
    "1.5" 1.5
    ".5"  0.5))

(t/deftest with-base
  (t/are [str num] (= (n/with-base 16 str) num)
    "F"   15.
    "FF"  255.
    "f"   15.
    "ff"  255.))
