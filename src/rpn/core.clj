(ns rpn.core
  (:use [rpn.commands :only [cmd? process-cmd]]
        [rpn.operators :only [operator? process-op]]
        [rpn.numbers :only [num? process-num]]
        [rpn.stack :only [stack-size]]
        [rpn.modifiers :only [*last-mod* modifier? process-mod trigger-mod]])
  (:require [clojure.string :as s])
  (:gen-class))

(def
  ^{:doc "The command prompt. Substitutions are done via fill-in"}
  *prompt* 
  "cljrpn[s::SIZE:]> ")

(defn- fmt-spec [string pat f]
  "Do pattern substitution on the string. Replaces pat with the result of f"
  (s/replace string pat (str (f))))

;This could probably do with some macrofying
(defn- fill-in [string]
  "Do pattern substition on the prompt"
  (-> string (fmt-spec ":SIZE:" stack-size)))

(defn print-prompt []
  "Prints the prompt."
  (print (fill-in *prompt*))
  (flush))

(defn- get-line []
  "Prompts the user and returns the input lowercased."
  (print-prompt)
  (if-let [line (read-line)] (.toLowerCase line)))

(defn process-token [tok]
  "Handle one token of input."
  (cond
    @*last-mod* (trigger-mod tok)
    (modifier? tok) (process-mod tok)
    (num? tok) (process-num tok)
    (operator? tok) (process-op tok)
    (cmd? tok) (process-cmd tok)
    true (println (str "FALLTHROUGH: " tok))))

(defn process-line [line]
  "Handle one line of input from the user"
  (let [tokens (s/split line #"\s+")]
    (doseq [tok tokens]
      (process-token tok)))
  (trigger-mod))


(defn- greeting []
  "Display the startup header."
  (println "cljrpn version 0.1.0 by Zach Raines"))

(defn main-loop [options]
  "The big show."
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
