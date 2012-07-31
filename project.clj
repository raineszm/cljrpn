(defproject cljrpn "0.1.0"
  :description "A simple RPN calculator"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.cli "0.2.0"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 [midje "1.3.0-SNAPSHOT"]]
  :checksum-deps true
  :main cljrpn.core)
