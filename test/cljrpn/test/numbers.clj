(ns cljrpn.test.numbers
  (:use cljrpn.numbers
        cljrpn.state
        midje.sweet)
  (:import cljrpn.state.State)
  (:use cljrpn.test.helpers))

(tabular "The finer points of number matching"
         (fact ?number => num?)
         ?number
         "1" "1.0" ".5" "5." "-4" "-4.3" "-.5" "1e0"
         "1e5" "1e-5" "-3e2" "-4e-3" "0.5e2" "1.6e17")

(tabular "The finer points of number matching (unwanted matches)"
         (fact ?number =not=> num?)
         ?number
         "apple" "f" "1.1." "1..0" "1.0." 
         "." "-." ".e4")

(tabular "Hexidemical recognition"
         (fact ?val ?arrow hex?) 
         ?val         ?arrow
         "a"          =>
         "A"          =>
         "0A"         =>
         "deadbeef"   =>
         "3g"         =not=>
         "waffle"     =not=>
         "2.0"        =not=>)
         

(tabular "Proper processing of numbers"
         (fact (process-num t-empty-state ?str) => (stack-t '(?num)))
         ?str  ?num
         "3"   3.0
         "1.5" 1.5
         ".5"  0.5)

(fact "Numbers are added to the stack"
      (process-num (State. '(1)) "2.5") => (stack-t '(2.5 1)))

(tabular "Proper processing of numbers"
         (fact (with-base 16 ?str) => ?num)
         ?str  ?num
         "F"   15.
         "FF"  255.
         "f"   15.
         "ff"  255.)
