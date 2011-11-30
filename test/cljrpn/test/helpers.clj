(ns cljrpn.test.helpers
  (:use cljrpn.state)
  (:use midje.sweet))

(def t-empty-state (new-state '()))

(defn stack-t [stack]
  (chatty-checker [state]
                  ((contains {:stack stack}) state)))

(defn register-t [reg value]
  (chatty-checker [state]
                  ((contains {reg value}) (:registers state))))
