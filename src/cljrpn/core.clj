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
  "The command prompt. Substitutions are done via fill-in. In the future this should be configuratble by the user."
  "cljrpn[s::SIZE:]> ")

(defmacro filter-proc [& substitutions]
  "Substitutions is a list of the form (pattern1 f1 pattern2 f2...)
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
  "Prints the prompt. A new-line is not appended."
  (print (fill-in prompt))
  (flush))

(defn- get-line [prompt]
  "Prompts the user and returns the input as a lowercase string."
  (print-prompt prompt)
  (if-let [line (read-line)] (.toLowerCase line)))

(defn process-token [stack tok]
  "Handle one token of input."
  (or
    (cond
    @*last-mod* (trigger-mod stack tok)
    (modifier? tok) (process-mod stack tok)
    (num? tok) (process-num stack tok)
    (operator? tok) (process-op stack tok)
    (cmd? tok) (process-cmd stack tok)
    :else (println (str "Unrecognized command: " tok) 
                  "For help try: ?"))
    stack))

(defn process-line [stack line]
  "Handle one line of input from the user. Input is split on white space and each \"word\" ithen processed as a token."
  (loop [stack stack
         tokens (s/split line #"\s+")]
    (if (empty? tokens)
      (trigger-mod stack)
      (recur
        (process-token stack (first tokens))
        (rest tokens)))))
    

(defn- greeting []
  "Display the startup header."
  (print-version))

(defn main-loop [options]
  "The big show. The options hash is here to allow for an rc file in future versions."
  (let [prompt (or (:prompt options) *prompt*)]
    (greeting)
    (loop [main-stack '()
           line (get-line prompt) ]
      (if (nil? line)
        0
        (recur (process-line main-stack line)
               (get-line prompt)))))
  (println "Exiting..."))

(defn -main [& args]
  (let [[flags args banner]
        ; Parse our command line arguments
        (cli
          args
          ["-e" "--execute" "Execute the supplied commands and then exit" :default nil]
          ["-v" "--version" "Print the version string" :default false :flag true]
          ["-h" "--help" "Display this help dialog" :default false :flag true])]
    (cond
      (:help flags) (println banner)
      (:execute flags) (process-line (:execute flags))
      (:version flags) (print-version)
      :else (main-loop {}))))
