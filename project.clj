(defproject cljrpn "0.1.0"
  :description "A simple RPN calculator"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.reader "0.8.13"]]
  :profiles {:uberjar {:aot :all},
             :dev {:dependencies [[midje "1.6.3"]]}}
  :checksum-deps true
  :main cljrpn.core)
