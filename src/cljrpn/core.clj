(ns cljrpn.core
  (:use [cljrpn.commands :only [cmd? process-cmd]]
        [cljrpn.operators :only [operator? process-op]]
        [cljrpn.numbers :only [num? process-num]]
        [cljrpn.stack :only [stack-size]]
        [cljrpn.modifiers :only [*last-mod* modifier? process-mod trigger-mod]]
        [clojure.tools.cli])
  (:require [clojure.string :as s])
  (:gen-class))

(def cljrpn-version
  "0.1.0")

(defn print-version []
  (println "cljrpn version" cljrpn-version "by Zach Raines"))

(def
  ^:dynamic
  *prompt*
  "The command prompt. Substitutions are done via fill-in"
  "cljrpn[s::SIZE:]> ")

(defmacro filter-proc [& substitutions]
  "Substitutions is a list of the form (pattern f pattern2 f2...)
  produces a method which replaces each supplied pattern in a string
  with the result of calling the corresponding function."
  (let [subst (apply hash-map substitutions) ]
  `(fn [string#]
    (-> string# ~@(map (fn [[pat f]]
                            `(s/replace ~pat (str (~f))))
                          subst)))))

(def fill-in
  (filter-proc
    ":SIZE:" stack-size))

(defn print-prompt [prompt]
  "Prints the prompt."
  (print (fill-in prompt))
  (flush))

(defn- get-line [prompt]
  "Prompts the user and returns the input lowercased."
  (print-prompt prompt)
  (if-let [line (read-line)] (.toLowerCase line)))

(defn process-token [tok]
  "Handle one token of input."
  (cond
    @*last-mod* (trigger-mod tok)
    (modifier? tok) (process-mod tok)
    (num? tok) (process-num tok)
    (operator? tok) (process-op tok)
    (cmd? tok) (process-cmd tok)
    :else (println (str "Unrecognized command: " tok) 
                  "For help try: ?")))

(defn process-line [line]
  "Handle one line of input from the user"
  (let [tokens (s/split line #"\s+")]
    (doseq [tok tokens]
      (process-token tok)))
  (trigger-mod))


(defn- greeting []
  "Display the startup header."
  (print-version))

(defn main-loop [options]
  "The big show."
  (let [prompt (or (:prompt options) *prompt*)]
    (greeting)
    (loop [line (get-line prompt)]
      (if (nil? line)
        0
        (do
          (process-line line)
          (recur (get-line prompt))))))
  (println "Exiting..."))

(defn -main [& args]
  (let [[flags args banner]
        (cli args
               ["-e" "--execute" "Execute the supplied commands and then exit" :default nil]
               ["-v" "--version" "Print the version string" :default false :flag true]
               ["-h" "--help" "Display this help dialog" :default false :flag true])]
    (cond
      (:help flags) (println banner)
      (:execute flags) (process-line (:execute flags))
      (:version flags) (print-version)
      :else (main-loop {}))))
