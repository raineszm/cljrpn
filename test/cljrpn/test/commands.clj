(ns cljrpn.test.commands
  (:use midje.sweet
        cljrpn.commands))

(fact "The swap command"
      (swap '(1 2 3 4)) => '(2 1 3 4))

(fact "The dup command"
      (dup '(1 2 3 4)) => '(1 1 2 3 4))

(fact "The clear command"
      (clear-stack '(1 2 3 4)) => '())

(fact "Pop modifies the stack"
      (stack-pop '(1 2 3 4)) => '(2 3 4))

(fact "Pop returns the top of the stack"
      (with-out-str
        (stack-pop '(1 2 3 4))) => (has-prefix "1"))
