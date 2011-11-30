(ns cljrpn.modifiers
  (:use [cljrpn.operators :only [op-table operator?]]
        [cljrpn.commands :only [cmd-table cmd? build-cmd]]
        [cljrpn.numbers :only [hex? binary? with-base]]
        [cljrpn.registers :only [set-register get-register register? used-registers]]
        [cljrpn.utils]
        cljrpn.state
        [clojure.string :only [replace-first]]))


(declare mod-table)

(defn print-help [table sym]
  (let [op ((keyword sym) table)
        aliases (:cmds op)
        effect (:help op)]
    (apply println "Aliases: "(map name aliases))
    (println effect))
  (newline))

(declare modifier?)

(defn help
  ([state]
   (doseq [[title hsh]
         [["Operators: " op-table]
           ["Commands: " cmd-table]
           ["Modifiers: " mod-table]]]
     (print title)
     (apply println (map name (sort (keys hsh))))
     (newline)))
  ([state sym]
   (cond
     (operator? sym) (print-help op-table sym)
     (cmd? sym) (print-help cmd-table sym)
     (modifier? sym) (print-help mod-table sym))))

(defn store
  ([state ]
   (println "Register not specified."))
  ([state r]
   (if (register? r)
     (do 
       (set-register r (top state))
       (popf state))
     (println r " is not a register."))))

(defn retrieve
  ([state]
   (println "Register not specified."))
  ([state r]
   (if (register? r)
     (if-let [v (get-register r)]
       (pushf state v))
     (println r " is not a register."))))

(defn register-show
  ([state]
   (let [regs (used-registers)]
     (doseq [[reg v] regs]
       (println (name reg) " <- " v))))
  ([state r]
   (if (register? r)
     (println r " <- " (get-register r))
     (println r " is not a valid register"))))

(defmacro literal [code pred base]
  (let [type-name (replace-first (str pred) "?" "")]
    [ code
     `(fn
       ([state# ]
        (println "Incomplete specifiction for " ~code))
       ([state# sym#]
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

(defn modifier? [m]
  (contains? mod-table (keyword m)))

(defn process-mod [state m]
  (assoc state :last-mod (-> (keyword m) mod-table :cmd)))

(defn trigger-mod
  ([state]
   (if-let [last-mod (:last-mod state)]
     (let [state (or (last-mod state) state)]
       (assoc state :last-mod nil))
     state))
  ([state sym]
   (let [state (or ((:last-mod state) state sym) state)]
     (assoc state :last-mod nil))))
