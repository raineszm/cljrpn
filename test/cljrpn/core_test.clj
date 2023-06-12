(ns cljrpn.core-test
  (:require
   [cljrpn.core :as c]
   [cljrpn.state :as s]
   [cljrpn.test-helpers :as th]
   [clojure.test :as t]))

(t/deftest process-token

  (t/testing "Commands are triggered by process token"
    (th/stack= (c/process-token (s/new-state '(1 2)) "clear") []))

  (t/testing "Numbers are relayed by process-token"
    (th/stack= (c/process-token (s/new-state []) "1") [1.])

    (t/testing "Operators are relayed by process-token"
      (th/stack= (c/process-token (s/new-state '(1 2)) "+") [3]))))

(t/deftest process-line
  (t/testing "Modifiers are triggered on following input."
    (th/stack= (c/process-line (s/new-state []) "x: A") [10.]))

  (t/testing "Contents of registers are listed"
    (t/is (=
           (with-out-str (c/process-line (s/new-state []) "3 -> a .r"))
           "a  <-  3.0\n")))

  (t/are
   [line val]
   (th/stack= (c/process-line (s/new-state []) line) [val])
    "1 2 +" 3.0
    "2 3 *" 6.0
    "3 2 -" 1.0
    "6 3 /" 2.0
    "2 3 ^" 8.0))
