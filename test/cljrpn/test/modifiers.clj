(ns cljrpn.test.modifiers
  (:use cljrpn.modifiers
        cljrpn.registers
        cljrpn.state
        cljrpn.test.helpers
        midje.sweet))

(fact "The store modifier places to top value of the stack into the specified register"
      (store (new-state '(..val..)) "a") => (register-t :a ..val..))

(fact "The retrieve modifer pushes the requested register to the stack."
      (retrieve
        (set-register
          t-empty-state
          "a"
          ..val..)
        "a") => (stack-t '(..val..)))
