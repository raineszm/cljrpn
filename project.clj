(defproject cljrpn "0.1.0"
  :description "A simple RPN calculator"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [midje "1.6-beta1"]]
  :checksum-deps true
  :main cljrpn.core)
