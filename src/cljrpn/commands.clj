(ns cljrpn.commands
  "Provides commands to the user which take no arguments such as quit"
  (:use [cljrpn.stack]
        [cljrpn.operators :only [*operators*]])
  (:use [cljrpn.utils]))

(defn stack-pop []
  "Pop a value from the stack and print it"
  (let [val (popf)]
    (if val
      (println val)
      (println "The stack is empty..."))))

(defn stack-show []
  "Print the entire contents of the main stack."
  (let [size (stack-size)]
    (println "STACK:")
    (println "(TOP)")
    (loop [i 0]
      (when-not (= i size)
        (println i ": " (nth @*main-stack* i))
        (recur (inc i))))))

(defn dup []
  "Duplicate the top of the stack"
  (if-let [bot (peek @*main-stack*)]
    (pushf bot)))

(defn swap []
  "Swap the top two values on the stack"
  (when (>= (stack-size) 2)
    (apply pushf (popf 2))))

(defn build-cmd [kwargs cmd help]
  "Allows aliases for a command to be specified."
  (let [kwargs (as-vec kwargs)]
    (mapcat (fn [kw]
              [(keyword kw) {:cmd cmd :cmds kwargs :help help}]) kwargs)))

(def
  ^{:doc "Map of the commands available and their attributes. Commands are
  accessed by the text used to issue them in the interpreter."}
  ^:dynamic
  *cmds*
  (construct build-cmd
    [:. stack-pop "Pops and prints the top of the stack"]
    [:.s stack-show "Prints the whole stack"]
    [:dup dup (effect "a" "a a")]
    [:swap swap (effect "a b" "b a")]
    [[:clear :.c] clear-stack "Empties the stack"]
    [[:q :quit :.q] #(System/exit 0) "Exits the program"]))

(defn cmd? [o]
  "Determine if the string o names a valid command."
  (contains? *cmds* (keyword o)))

(defn process-cmd [o]
  "Handle one command frome the user"
  ((-> (keyword o) *cmds* :cmd)))

