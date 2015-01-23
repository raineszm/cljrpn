(ns cljrpn.commands
  "Provides commands to the user which take no arguments, such as quit"
  (:require [cljrpn.utils :refer [as-vec construct effect]]
        [cljrpn.state :refer [pushf popf top]]))

(defn stack-pop
  "Pop a value from the stack and print it. Returns the updated state."
  [state]
  (if-let [top' (top state)]
    (do
      (println top')
      (popf state))
    (println "The stack is empty...")))

(defn stack-show
  "Print the entire contents of the main stack. Returns nil"
  [state]
  (let [stack (:stack state)
        size (count stack)]
    (println "STACK:")
    (println "(TOP)")
    (doseq [i (range size)]
        (println i ": " (nth stack i)))))

(defn dup
  "Duplicate the top of the stack. Returns the updated state"
  [state]
  (if-let [top' (top state)]
    (pushf state top')))

(defn swap
  "Swap the top two values on the stack. Returns the updated state."
  [state]
  (let [stack (:stack state)]
    (when (>= (count stack) 2)
      (assoc state
             :stack
             (concat (reverse (take 2 stack)) (drop 2 stack))))))

(defn clear-stack
  "Clears the stack."
  [state]
  (assoc state :stack '()))

(defn quit
  "Terminates the program."
  [state]
  (System/exit 0))

(defn build-cmd
  "Expands the arguments into a collection of entries for the command table.
  _kwargs_ is a list of keywords which should be intrepreted as this supplied command
  _cmd_ is a command to be run. Commands should take a state as their only argument and return an updated state (or nil if the state has not changed).
  _help_ is the appropriate help text."
  [kwargs cmd help]
  (let [kwargs (as-vec kwargs)]
    (mapcat
      (fn [kw]
        [(keyword kw)
         {:cmd cmd
          :cmds kwargs
          :help help}])
      kwargs)))

(def
  cmd-table
  "Map of the commands available and their attributes. Commands are
  accessed by the text used to issue them in the interpreter."
  (construct build-cmd
    [:. stack-pop "Pops and prints the top of the stack"]
    [:.s stack-show "Prints the whole stack"]
    [:dup dup (effect "a" "a a")]
    [:swap swap (effect "a b" "b a")]
    [[:clear :.c] clear-stack "Empties the stack"]
    [[:q :quit :.q] quit "Exits the program"]))

(defn cmd?
  "Determine if the string _o_ names a valid command."
  [o]
  (contains? cmd-table (keyword o)))

(defn process-cmd
  "Handle one command frome the user"
  [state o]
  ((-> (keyword o) cmd-table :cmd) state))

