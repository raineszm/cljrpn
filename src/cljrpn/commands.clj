(ns cljrpn.commands
  "Provides commands to the user which take no arguments such as quit"
  (:use [cljrpn.utils]
        cljrpn.state))

(defn stack-pop [state]
  "Pop a value from the stack and print it"
  (if-let [top' (top state)]
    (do
      (println top')
      (popf state))
    (println "The stack is empty...")))

(defn stack-show [state]
  "Print the entire contents of the main stack."
  (let [stack (:stack state)
        size (count stack)]
    (println "STACK:")
    (println "(TOP)")
    (doseq [i (range size)]
        (println i ": " (nth stack i)))))

(defn dup [state]
  "Duplicate the top of the stack"
  (if-let [top' (top state)]
    (pushf state top')))

(defn swap [state]
  "Swap the top two values on the stack"
  (let [stack (:stack state)]
    (when (>= (count stack) 2)
      (assoc state
             :stack
             (concat (reverse (take 2 stack)) (drop 2 stack))))))

(defn clear-stack [state]
  "Clears the stack."
  (assoc state :stack '()))

(defn quit [state]
  (System/exit 0))

(defn build-cmd [kwargs cmd help]
  "Takes a list of the form [kwargs cmd help] and expands this into a collection of entries for the command table. Kwargs is a list of keywords which should be intrepreted as this supplied command, cmd is a command to be run, and help is the appropriate help text."
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

(defn cmd? [o]
  "Determine if the string _o_ names a valid command."
  (contains? cmd-table (keyword o)))

(defn process-cmd [state o]
  "Handle one command frome the user"
  ((-> (keyword o) cmd-table :cmd) state))

