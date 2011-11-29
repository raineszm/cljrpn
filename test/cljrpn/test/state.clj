(ns cljrpn.test.state
  (:use cljrpn.state)
  (:use cljrpn.test.helpers)
  (:use midje.sweet))

(fact "Pushf a should add to the top of the stack"
  (pushf t-empty-state 1)=> (contains {:stack '(1)}))

(unfinished g)

(fact "update-stack replace the stack"
  (update-stack (new-state '()) g) => (contains {:stack '(4)})
  (provided
    (g anything) => '(4)))

(fact "top fetches the top of the stack"
  (top (new-state '(1 2 3))) => 1)

(fact "popf removes the top of the stack"
  (popf (new-state '(1 2 3))) => (contains {:stack '(2 3)}))
