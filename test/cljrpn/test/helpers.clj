(ns cljrpn.test.helpers
  (:use [cljrpn.stack :only [clear-stack popf]]))

(defmacro through-stack [form]
  `(do ~form
     (popf)))

(defn prepare []
  (clear-stack))

