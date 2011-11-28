(ns cljrpn.test.operators
  (:use cljrpn.operators
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
