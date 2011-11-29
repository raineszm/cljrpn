(ns cljrpn.test.operators
  (:use cljrpn.operators
        cljrpn.state
        cljrpn.test.helpers
        midje.sweet))

(fact "Should build an entry for the operator map from a operator spec"
      (build-op :+ + "+" 2) =>
      (just
        [:+ {:op +
             :help "+"
             :arity 2
             :cmds [:+]}]))

(fact "Should build multiple entries when first argument of spec is a vector"
      (build-op [:+ :plus] + "+" 2) =>

      (let [op-map {:op +
                    :help "+"
                    :arity 2
                    :cmds [:+ :plus]}]
        [:+ op-map
         :plus op-map]))


(fact "Should produce stack effect when given a vector for help"
      (let [built-op (build-op :+ + ["x y" "x + y"] 2)]
        (:help (second built-op)) => "(STACK EFFECT) [... x y ] -> [... x + y ]"))

(fact "Apply-op"
      (apply-op (new-state '(1 2)) + 2) => (stack-t '(3))
      (apply-op (new-state '(1)) + 2) => falsey)

(tabular "Argument counting"
         (fact (process-op (new-state ?stack) ?op) => (stack-t ?result))
         ?stack     ?op   ?result
         '(1 2)     "+"   '(3)
         '(1 2 3 4) "sum" '(10)
         '(1 2 3 4) "prod" '(24))

(tabular "Arithmetic behavior"
         (fact (process-op (new-state ?stack) ?op) => (stack-t ?result))
         ?stack   ?op   ?result
         '(1 2 4) "+"   '(3 4)
         '(1 2 4) "-"   '(1 4)
         '(1 2 4) "*"   '(2 4)
         '(1 2 4) "/"   '(2 4)
         '(1 2 4) "neg" '(-1 2 4)
         '(2 2 3) "^"   '(4 3))

