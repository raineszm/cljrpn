(defproject cljrpn "0.1.0"
  :description "A simple RPN calculator"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.reader "1.3.0"]]
  :resource-paths ["resources/"]
  :profiles {:uberjar {:aot :all},
             :dev {:dependencies [[midje "1.9.3"]]}}
  :checksum-deps true
  :main cljrpn.core)
