(ns cljrpn.test.fixtures
  (:use [cljrpn.stack :only [clear-stack popf]]))

(defmacro through-stack [form]
  `(do ~form
     (popf)))

(defn clear [f]
  (clear-stack)
  (f))

