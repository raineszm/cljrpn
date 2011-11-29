(ns cljrpn.test.helpers
  (:use cljrpn.state)
  (:use midje.sweet)
  (:import cljrpn.state.State))

(def t-empty-state (State. '()))

(defn stack-t [stack]
  (chatty-checker [state]
                  ((contains {:stack stack}) state)))
