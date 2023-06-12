(ns cljrpn.commands-test
  (:require
   [cljrpn.commands :as c]
   [cljrpn.state :as s]
   [cljrpn.test-helpers :as th]
   [clojure.test :as t]))

(def state (s/new-state '(1 2 3 4)))

(t/deftest swap
  (th/stack= (c/swap state) [2 1 3 4]))

(t/deftest dup
  (th/stack= (c/dup state) [1 1 2 3 4]))
;
(t/deftest clear
  (t/is (empty?
         (:stack (c/clear-stack state)))))

(t/deftest stack-pop
  (t/testing "Pop modifies the stack"
    (with-out-str ; suppress printing the value
      (th/stack= (c/stack-pop state) [2 3 4])))

  (t/testing "Pop returns the top of the stack"
    (t/is (=
           (with-out-str
             (c/stack-pop state)) "1\n"))))
