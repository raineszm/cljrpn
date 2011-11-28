(ns cljrpn.modifiers
  (:use [cljrpn.operators :only [op-table operator?]]
        [cljrpn.commands :only [cmd-table cmd? build-cmd]]
        [cljrpn.numbers :only [hex? binary? with-base]]
        [cljrpn.registers :only [set-register get-register register? used-registers]]
        [cljrpn.utils]
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
  ([stack]
   (doseq [[title hsh]
         [["Operators: " op-table]
           ["Commands: " cmd-table]
           ["Modifiers: " mod-table]]]
     (print title)
     (apply println (map name (sort (keys hsh))))
     (newline)))
  ([stack sym]
   (cond
     (operator? sym) (print-help op-table sym)
     (cmd? sym) (print-help cmd-table sym)
     (modifier? sym) (print-help mod-table sym))))

(defn store
  ([stack ]
   (println "Register not specified."))
  ([stack r]
   (if (register? r)
     (do 
       (set-register r (first stack))
       (rest stack))
     (println r " is not a register."))))

(defn retrieve
  ([stack]
   (println "Register not specified."))
  ([stack r]
   (if (register? r)
     (if-let [v (get-register r)]
       (conj stack v))
     (println r " is not a register."))))

(defn register-show
  ([stack]
   (let [regs (used-registers)]
     (doseq [[reg v] regs]
       (println (name reg) " <- " v))))
  ([stack r]
   (if (register? r)
     (println r " <- " (get-register r))
     (println r " is not a valid register"))))

(defmacro literal [code pred base]
  (let [type-name (replace-first (str pred) "?" "")]
    [ code
     `(fn
       ([stack# ]
        (println "Incomplete specifiction for " ~code))
       ([stack# sym#]
        (if-not (~pred sym#)
          (println "Malformed command " ~code " " sym#)
          (conj stack# (with-base ~base sym#)))))
     (str "Interprets the next literal as a " type-name " integer")]))

(def ^:dynamic *last-mod* (atom nil))

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

(defn process-mod [stack m]
  (reset! *last-mod* (-> (keyword m) mod-table :cmd))
  stack)

(defn trigger-mod
  ([stack]
   (if @*last-mod*
     (let [stack (@*last-mod* stack)]
       (reset! *last-mod* nil)
       stack)
     stack))
  ([stack sym]
   (let [new-stack (@*last-mod* stack sym)]
     (reset! *last-mod* nil)
     (or new-stack stack))))
