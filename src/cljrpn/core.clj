(ns cljrpn.core
  (:require [cljrpn.commands :refer [cmd? process-cmd]]
        [cljrpn.operators :refer [operator? process-op]]
        [cljrpn.numbers :refer [num? process-num]]
        [cljrpn.modifiers :refer [modifier? process-mod trigger-mod]]
        [cljrpn.state :refer [new-state stack-size]]
        [clojure.tools.cli :refer :all]
        [clojure.string :as s])
  (:gen-class))

(def cljrpn-version
  "0.1.0")

(defn print-version []
  (println "cljrpn version" cljrpn-version "by Zach Raines"))

(def
  ^:dynamic
  *prompt*
  "The command prompt. Substitutions are done via fill-in. In the future this should be configurable by the user."
  "cljrpn[s::SIZE:]> ")

(defmacro filter-proc
  "Substitutions is a list of the form (pattern1 f1 pattern2 f2...)
  produces a method which replaces each supplied pattern in a string
  with the result of calling the corresponding function."
  [& substitutions]
  (let [subst (apply hash-map substitutions)
        state-var (gensym)]
    ;create a new function which takes a string and the state object
  `(fn [~state-var string#]
     ;iteratively replace the given patterns in the string by the result
     ;of running the corresponding function on the state object
    (-> string# ~@(map
                    ;generate the following function
                    (fn [[pat f]]
                            ;replace each pattern 'pat' in the list
                            `(s/replace ~pat
                                       ;with the result of calling f on the
                                        ;state var
                                        (str (~f ~state-var))))
                    ;for each pair in the list substitutions
                          subst)))))

(def fill-in
  (filter-proc
    ":SIZE:" stack-size))

(defn print-prompt
  "Prints the prompt. A new-line is not appended."
  [state prompt]
  (print (fill-in state prompt))
  (flush))

(defn- get-line
  "Prompts the user and returns the input as a lowercase string."
  [state prompt]
  (print-prompt state prompt)
  (if-let [line (read-line)] (.toLowerCase line)))

(defn process-token
  "Handle one token of input."
  [state tok]
  (or
    (cond
    (:last-mod state) (trigger-mod state tok)
    (modifier? tok) (process-mod state tok)
    (num? tok) (process-num state tok)
    (operator? tok) (process-op state tok)
    (cmd? tok) (process-cmd state tok)
    :else (println "Unrecognized command: " tok
                  "For help try: ?"))
    state))

(defn process-line
  "Handle one line of input from the user. Input is split on white space and each \"word\" is then processed as a token."
  [state line]
  (loop [state state
         tokens (s/split line #"\s+")]
    (if (empty? tokens)
      (trigger-mod state)
      (recur
        (process-token state (first tokens))
        (rest tokens)))))


(defn- greeting
  "Display the startup header."
  []
  (print-version))

(defn main-loop
  "The big show. The options hash is here to allow for an rc file in future versions."
  [options]
  (let [prompt (or (:prompt options) *prompt*)]
    (greeting)
    (loop [main-state (new-state '())
           line (get-line main-state prompt) ]
      (if (nil? line)
        0
        (let [new-state (process-line main-state line)]
          (recur new-state
                 (get-line new-state prompt))))))
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
      (:execute flags) (and (process-line (new-state '()) (:execute flags)) nil)
      (:version flags) (print-version)
      :else (main-loop {}))))
