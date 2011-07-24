(ns rpn.core
  (:use [rpn.commands :only [cmd? process-cmd]]
        [rpn.operators :only [operator? process-op]]
        [rpn.numbers :only [num? process-num]]
        [rpn.modifiers :only [*last-mod* modifier? process-mod trigger-mod]])
  (:use [clojure.string :only [split]])
  (:gen-class))

(def *prompt* "cljrpn> ")

(defn print-prompt []
  (print *prompt*)
  (flush))

(defn- get-line []
  (print-prompt)
  (.toLowerCase (read-line)))

(defn process-token [tok]
  (cond
    @*last-mod* (trigger-mod tok)
    (modifier? tok) (process-mod tok)
    (num? tok) (process-num tok)
    (operator? tok) (process-op tok)
    (cmd? tok) (process-cmd tok)
    true (println (str "FALLTHROUGH: " tok))))

(defn process-line [line]
  (let [tokens (split line #"\s+")]
    (doseq [tok tokens]
      (process-token tok)))
  (trigger-mod))


(defn- greeting []
  (println "cljrpn version 0.1.0 by Zach Raines"))

(defn main-loop [options]
  (greeting)
  (loop [line (get-line)]
    (if (nil? line)
      0
      (do
        (process-line line)
        (recur (get-line)))))
  (println "Exiting..."))

(defn -main [& args]
  (main-loop {}))
