(ns cljrpn.test.core
  (:use [cljrpn.core])
  (:use midje.sweet)
  (:use [clojure.string :only [trim-newline]]))


(tabular "Basic arithmetic"
         (fact (trim-newline (with-out-str (process-line ?line))) => ?val)
         ?line      ?val
         "1 2 + ." "3.0"
         "2 3 * ." "6.0"
         "3 2 - ." "1.0"
         "6 3 / ." "2.0"
         "2 3 ^ ." "8.0")
