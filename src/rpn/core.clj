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

(defmacro filter-proc [& substitutions]
  "Substitutions is a list of the form (pattern f pattern2 f2...)
  produces a method which replaces each supplied pattern in a string
  with the result off calling the corresponding function."
  (let [subst (apply hash-map substitutions) ]
  `(fn [string#]
    (-> string# ~@(map (fn [[pat f]]
                            `(s/replace ~pat (str (~f))))
                          subst)))))

(def fill-in
  (filter-proc
    ":SIZE:" stack-size))

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
    true (println (str "Unrecognized command: " tok) 
                  "For help try: ?")))

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
