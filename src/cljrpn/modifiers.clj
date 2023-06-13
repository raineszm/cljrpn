(ns cljrpn.modifiers
  "Operations which modify the behavior of the next token to be parsed"
  (:require [cljrpn.operators :refer [op-table operator?]]
            [cljrpn.commands :refer [cmd-table cmd? build-cmd]]
            [cljrpn.numbers :refer [hex? binary? with-base]]
            [cljrpn.registers :refer [set-register get-register register? used-registers]]
            [cljrpn.utils :refer [construct]]
            [cljrpn.state :refer [pushf popf top]]
            [clojure.string :refer [replace-first]]))

(declare mod-table)

(defn print-help
  "Display the help entry for _sym_ from command table _table_"
  [table sym]
  (let [op ((keyword sym) table)
        aliases (:cmds op)
        effect (:help op)]
    (apply println "Aliases: " (map name aliases))
    (println effect))
  (newline))

(declare modifier?)

(defn help
  "If called with only the state, prints out a table for all accepted commands. If called with an additional symbol, attempts to print the help for that symbol."
  ([_]
   (doseq [[title hsh]
           [["Operators: " op-table]
            ["Commands: " cmd-table]
            ["Modifiers: " mod-table]]]
     (print title)
     (->> hsh keys sort (map name) (apply println))
     (newline)))
  ([_ sym]
   (when-let [table (cond
                      (operator? sym) op-table
                      (cmd? sym) cmd-table
                      (modifier? sym) mod-table)]
     (print-help table sym))))

(defn store
  "Store the top of the stack to the register _r_"
  ([_]
   (println "Register not specified."))
  ([state r]
   (if (register? r)
     (popf (set-register state r (top state)))
     (println r " is not a register."))))

(defn retrieve
  "Push the value of the register _r_ to the stack"
  ([_]
   (println "Register not specified."))
  ([state r]
   (if (register? r)
     (when-let [v (get-register state r)]
       (pushf state v))
     (println r " is not a register."))))

(defn register-show
  "If called with no arguments displays the value of all set registers. If called with an argument, displays the value of the supplied register"
  ([state]
   (let [regs (used-registers state)]
     (doseq [[reg v] regs]
       (println (name reg) " <- " v))))
  ([state r]
   (if (register? r)
     (println r " <- " (get-register state r))
     (println r " is not a valid register"))))

(defmacro literal
  "Returns a DSL entry to generate a command which will interpret the next literal in a particular base."
  [code pred base]
  (let [type-name (replace-first (str pred) "?" "")]
    [code ;Prefix which can be used within the program
     ;; The function which will parse the following symbol
     ;; in the proper base
     `(fn
        ([state#]
         (println "Incomplete specifiction for " ~code))
        ([state# sym#]
        ;check to see if _sym#_ is a valid literal of base _base_
         (if-not (~pred sym#)
           (println "Malformed command " ~code " " sym#)
           (pushf state# (with-base ~base sym#)))))
     (str "Interprets the next literal as a " type-name " integer")]))

(def mod-table
  (construct build-cmd
             [[:? :h :help] help
              (str "When called with an argument displays help"
                   " information about that command. Otherwise, "
                   "displays a command list.")]
             ["->" store
              (str "Stores the top value of the stack to the register "
                   "specified as the following token. Registers are a-z")]
             ["<-" retrieve
              (str "Pushes the register "
                   "specified as the following token to the stack. Registers are a-z")]
             [".r" register-show
              (str "Prints the contents of "
                   "register specified as the following token. Register are a-z")]
             (literal "x:" hex? 16)
             (literal "b:" binary? 2)))

(defn modifier?
  "Determines whether the token _m_ defines a valid modifier command."
  [m]
  (contains? mod-table (keyword m)))

(defn process-mod
  "Arms the modifier _m_ so that it can process the next read token"
  [state m]
  (assoc state :last-mod (get-in mod-table [(keyword m) :cmd])))

(defn trigger-mod
  "Triggers the currently armed modifier (if any). If no token is available a fallback mode is executed, otherwise the modifier consumes the token."
  [state & sym]
  (if-let [last-mod (:last-mod state)]
    (assoc (or (apply last-mod state sym) state)
           :last-mod nil)
    state))
