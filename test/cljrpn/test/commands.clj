(ns cljrpn.test.commands
  (:use midje.sweet
        cljrpn.state
        cljrpn.commands)
  (:import cljrpn.state.State)
  (:use cljrpn.test.helpers))

(def state (State. '(1 2 3 4 )))

(fact "The swap command"
      (swap state) => (stack-t '(2 1 3 4)))

(fact "The dup command"
      (dup state) => (stack-t '(1 1 2 3 4)))

(fact "The clear command"
      (clear-stack state) => (stack-t '()))

(fact "Pop modifies the stack"
      (with-out-str
        (stack-pop state) => (stack-t '(2 3 4))))

(fact "Pop returns the top of the stack"
      (with-out-str
        (stack-pop state)) => (has-prefix "1"))
