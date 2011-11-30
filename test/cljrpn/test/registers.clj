(ns cljrpn.test.registers
  (:use cljrpn.registers
        cljrpn.state
        cljrpn.test.helpers
        midje.sweet))

(fact "Registers can be set"
      (set-register t-empty-state "a" 3) => (register-t :a 3))

(fact "Registers can be retrived"
      (get-register
       (assoc-in t-empty-state
                  [:registers :a] 3)
        "a") => 3)
      
