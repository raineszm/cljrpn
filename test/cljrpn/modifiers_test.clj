(ns cljrpn.modifiers-test
  (:require
   [cljrpn.modifiers :as m]
   [cljrpn.state :as s]
   [cljrpn.registers :as r]
   [cljrpn.test-helpers :as th]
   [clojure.test :as t]))

(t/deftest store
  (let [state
        (m/store (s/new-state [6]) "a")]
    (th/register= state :a 6)))

(t/deftest retrieve
  (let [state (r/set-register (s/new-state []) "a" 14)]
    (th/stack= (m/retrieve state "a") [14])))
