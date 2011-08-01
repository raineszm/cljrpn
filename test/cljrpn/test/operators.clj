(ns cljrpn.test.operators
  (:use [cljrpn.operators]
        [cljrpn.test.fixtures]
        [clojure.test]))

(use-fixtures :once disconnect)

(deftest test-build-op
         (is (=
               (build-op :+ + "+" 2)
               [:+ {:op +
                    :help "+"
                    :arity 2
                    :cmds [:+]}])
             "Should build an entry for the operator map from a operator spec")
         (is (=
               (build-op [:+ :plus] + "+" 2)
               (let [op-map {:op +
                             :help "+"
                             :arity 2
                             :cmds [:+ :plus]}]
                 [:+ op-map
                  :plus op-map]))
             "Should build multiple entries when first argument of spec is a vector")
         (is 
           (let [built-op (build-op :+ + ["x y" "x + y"] 2)]
           (=
               (:help (second built-op))
               "(STACK EFFECT) [... x y ] -> [... x + y ]"))
           "Should produce stack effect when given a vector for help"))

