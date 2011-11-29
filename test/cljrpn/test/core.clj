(ns cljrpn.test.core
  (:use cljrpn.core
        cljrpn.state)
  (:use midje.sweet
        cljrpn.test.helpers))

(fact "Commands are triggered by process token"
      (process-token (new-state '(1 2)) "clear") => (stack-t '()))

(fact "Numbers are relayed by process-token"
      (process-token t-empty-state "1") => (stack-t '(1.)))

(fact "Operators are relayed by process-token"
      (process-token (new-state '(1 2)) "+") => (stack-t '(3)))

(fact "Modifiers are triggered on following input."
      (process-line t-empty-state "x: A") => (stack-t '(10.)))

(tabular "Basic arithmetic"
         (fact (process-line t-empty-state ?line) => (stack-t '(?val)))
         ?line      ?val
         "1 2 +" 3.0
         "2 3 *" 6.0
         "3 2 -" 1.0
         "6 3 /" 2.0
         "2 3 ^" 8.0)
