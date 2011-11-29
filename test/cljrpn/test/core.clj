(ns cljrpn.test.core
  (:use cljrpn.core)
  (:use midje.sweet))

(fact "Commands are triggered by process token"
      (process-token '(1 2) "clear") => '())

(fact "Numbers are relayed by process-token"
      (process-token '() "1") => '(1.))

(fact "Operators are relayed by process-token"
      (process-token '(1 2) "+") => '(3))

(fact "Modifiers are triggered on following input."
      (process-line '() "x: A") => '(10.))

(tabular "Basic arithmetic"
         (fact (process-line '() ?line) => (has-prefix ?val))
         ?line      ?val
         "1 2 +" 3.0
         "2 3 *" 6.0
         "3 2 -" 1.0
         "6 3 /" 2.0
         "2 3 ^" 8.0)
