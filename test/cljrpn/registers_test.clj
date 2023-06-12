(ns cljrpn.registers-test
  (:require
   [cljrpn.registers :as r]
   [cljrpn.state :as s]
   [cljrpn.test-helpers :as th]
   [clojure.test :as t]))

(t/deftest set-register
  (t/is
   (th/register=
    (r/set-register (s/new-state []) "a" 8)
    :a 8)))

(t/deftest get-register
  (let [state
        (assoc-in (s/new-state [])
                  [:registers :a]
                  11)]
    (t/is
     (= (r/get-register state "a")
        11))))

(t/deftest used-registers
  (let [state
        (assoc-in (s/new-state [])
                  [:registers :a]
                  11)]
    (t/is
     (= (r/used-registers state)
        [[:a 11]]))))
