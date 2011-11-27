(ns cljrpn.commands
  "Provides commands to the user which take no arguments such as quit"
  (:use [cljrpn.stack])
  (:use [cljrpn.utils]))

(defn stack-pop []
  "Pop a value from the stack and print it"
  (if-let [top (popf)]
      (println top)
      (println "The stack is empty...")))

(defn stack-show []
  "Print the entire contents of the main stack."
  (let [size (stack-size)]
    (println "STACK:")
    (println "(TOP)")
    (doseq [i (range size)]
        (println i ": " (nth @*main-stack* i)))))

(defn dup []
  "Duplicate the top of the stack"
  (if-let [bot (peek @*main-stack*)]
    (pushf bot)))

(defn swap []
  "Swap the top two values on the stack"
  (when (>= (stack-size) 2)
    (apply pushf (popf 2))))

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
    [[:q :quit :.q] #(System/exit 0) "Exits the program"]))

(defn cmd? [o]
  "Determine if the string _o_ names a valid command."
  (contains? cmd-table (keyword o)))

(defn process-cmd [o]
  "Handle one command frome the user"
  ((-> (keyword o) cmd-table :cmd)))

