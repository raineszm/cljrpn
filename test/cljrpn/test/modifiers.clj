(ns cljrpn.test.modifiers
  (:use cljrpn.modifiers
        cljrpn.registers
        cljrpn.state
        cljrpn.test.helpers
        midje.sweet))

(def t-state-w-registers
  (set-register
    t-empty-state
    "a"
    3))

(fact "The store modifier places to top value of the stack into the specified register"
      (store (new-state '(1)) "a") => (register-t :a 1))

(fact "The retrieve modifer pushes the requested register to the stack."
      (retrieve t-state-w-registers "a") => (stack-t '(3)))
