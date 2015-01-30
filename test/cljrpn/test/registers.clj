(ns cljrpn.test.registers
  (:use cljrpn.registers
        cljrpn.state
        cljrpn.test.helpers
        midje.sweet))

(fact "Registers can be set"
      (set-register t-empty-state "a" ..val..) => (register-t :a ..val..))

(fact "Registers can be retrived"
      (get-register
       (assoc-in t-empty-state
                  [:registers :a] ..val..)
        "a") => ..val..)

(fact "Used-registers returns those registers which are set"
       (used-registers
        (assoc-in t-empty-state
                  [:registers :a] ..val..)) => (contains [[:a ..val..]]))
