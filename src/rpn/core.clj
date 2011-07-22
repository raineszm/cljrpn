(ns rpn.core
  (:use [rpn.stack-ops :only [stack-op? process-stackop]]
        [rpn.operators :only [operator? process-op]]
        [rpn.stack :only [pushf]])
  (:use [clojure.string :only [split]])
  (:gen-class))

(def *prompt* "cljrpn> ")

(defn print-prompt []
  (print *prompt*)
  (flush))

(defn is-number? [tok]
  (re-matches #"(\d+\.?\d*)|(\d*\.?\d+)" tok))

(defn process-token [tok]
  (cond
    (is-number? tok) (pushf (Float/parseFloat tok))
    (operator? tok) (process-op tok)
    (stack-op? tok) (process-stackop tok)
    true (println (str "FALLTHROUGH: " tok))))

(defn process-line [line]
  (let [tokens (split line #"\s+")]
    (doseq [tok tokens]
      (process-token tok))))


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
