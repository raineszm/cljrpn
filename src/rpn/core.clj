(ns rpn.core
  (:use [rpn.commands :only [cmd? process-cmd]]
        [rpn.operators :only [operator? process-op]]
        [rpn.stack :only [pushf]]
        [rpn.modifiers :only [*last-mod* modifier? process-mod trigger-mod]])
  (:use [clojure.string :only [split]])
  (:gen-class))

(def *prompt* "cljrpn> ")

(defn print-prompt []
  (print *prompt*)
  (flush))

(defn num? [tok]
  (re-matches #"(\d+\.?\d*)|(\d*\.?\d+)" tok))

(defn process-num [tok]
  (pushf (Float/parseFloat tok)))

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


(defn main-loop [options]
  (print-prompt)
  (loop [line (read-line)]
    (if (nil? line)
      0
      (do
        (process-line line)
        (print-prompt)
        (recur (read-line)))))
  (println "Exiting..."))

(defn -main [& args]
  (main-loop {}))
