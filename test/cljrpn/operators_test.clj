(ns cljrpn.operators-test
  (:require
   [cljrpn.operators :as o]
   [cljrpn.state :as s]
   [cljrpn.test-helpers :as th]
   [clojure.test :as t]))

(t/deftest build-op)

; (fact "Should build an entry for the operator map from a operator spec"
;       (build-op :+ + "+" 2) =>
;       (just
;         [:+ {:op +
;              :help "+"
;              :arity 2
;              :cmds [:+]}]))
;
; (fact "Should build multiple entries when first argument of spec is a vector"
;       (build-op [:+ :plus] + "+" 2) =>
;
;       (let [op-map {:op +
;                     :help "+"
;                     :arity 2
;                     :cmds [:+ :plus]}]
;         [:+ op-map
;          :plus op-map]))
;
;
; (fact "Should produce stack effect when given a vector for help"
;       (let [built-op (build-op :+ + ["x y" "x + y"] 2)]
;         (:help (second built-op))
;         => "(STACK EFFECT) [... x y ] -> [... x + y ]"))
;

(t/deftest apply-op
  (th/stack= (o/apply-op (s/new-state '(1 2)) + 2) [3])
  (t/is (not (o/apply-op (s/new-state '(1)) + 2))))

(t/deftest process-op
  (t/testing "Argument counting"
    (t/are
     [stack op result]
     (= (:stack (o/process-op (s/new-state stack) op)) result)
      '(1 2)     "+"   '(3)
      '(1 2 3 4) "sum" '(10)
      '(1 2 3 4) "prod" '(24)))

  (t/testing "Arithmetic behavior"
    (t/are
     [stack op result]
     (= (:stack (o/process-op (s/new-state stack) op)) result)
      '(1 2 4) "+"   '(3 4)
      '(1 2 4) "-"   '(1 4)
      '(1 2 4) "*"   '(2 4)
      '(1 2 4) "/"   '(2 4)
      '(1 2 4) "neg" '(-1 2 4)
      '(2 2 3) "^"   '(4 3))))
