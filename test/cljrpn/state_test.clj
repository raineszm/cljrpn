(ns cljrpn.state-test
  (:require [cljrpn.state :as s]
            [cljrpn.test-helpers :as th]
            [clojure.test :as t]))

(comment (t/run-tests))

(t/deftest pushf
  (t/is
   (th/stack=
    (s/pushf (s/new-state []) 1)
    [1])))

(t/deftest update-stack
  (t/is
   (th/stack=
    (s/update-stack (s/new-state [1]) #(map inc %)) [2])))

(t/deftest top
  (t/is
   (= (s/top (s/new-state [1 2 3])) 1)))

(t/deftest popf
  (t/is
   (th/stack=
    (s/popf (s/new-state '(1 2 3)))
    [2 3])))
