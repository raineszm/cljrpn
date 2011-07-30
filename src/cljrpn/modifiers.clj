(ns cljrpn.modifiers
  (:use [cljrpn.operators :only [*operators* operator?]]
        [cljrpn.commands :only [*cmds* cmd? build-cmd]]
        [cljrpn.numbers :only [hex? binary? with-base]]
        [cljrpn.stack :only [pushf popf]]
        [cljrpn.registers :only [set-register get-register register? used-registers]]
        [cljrpn.utils]
        [clojure.string :only [replace-first]]))


(declare *modifiers*)

(defn- prep-key [k]
  (replace-first (str k) ":" ""))

(defn- prep-keys [ks]
  (map prep-key ks))

(defn print-help [table sym]
  (let [op ((keyword sym) table)
        aliases (:cmds op)
        effect (:help op)]
    (apply println "Aliases: "(prep-keys aliases))
    (println effect))
  (newline))

(declare modifier?)

(defn help
  ([]
   (doseq [[title hsh]
         [["Operators: " *operators*]
           ["Commands: " *cmds*]
           ["Modifiers: " *modifiers*]]]
     (print title)
     (apply println (prep-keys (keys hsh)))
     (newline)))
  ([sym]
   (cond
     (operator? sym) (print-help *operators* sym)
     (cmd? sym) (print-help *cmds* sym)
     (modifier? sym) (print-help *modifiers* sym))))

(defn store
  ([]
   (println "Register not specified."))
  ([r]
   (if (register? r)
     (set-register r (popf))
     (println r " is not a register."))))

(defn retrieve
  ([]
   (println "Register not specified."))
  ([r]
   (if (register? r)
     (if-let [v (get-register r)]
       (pushf v))
     (println r " is not a register."))))

(defn register-show
  ([]
   (let [regs (used-registers)]
     (doseq [[reg v] regs]
       (println (prep-key reg) " <- " v))))
  ([r]
   (if (register? r)
     (println r " <- " (get-register r))
     (println r " is not a valid register"))))

(defmacro literal [code pred base]
  (let [type-name (replace-first (str pred) "?" "")]
    [ code
     `(fn
       ([]
        (println "Incomplete specifiction for " ~code))
       ([sym#]
        (if-not (~pred sym#)
          (println "Malformed command " ~code " " sym#)
          (pushf (with-base ~base sym#)))))
     (str "Interprets the next literal as a " type-name " integer")]))

(def *last-mod* (atom nil))

(def *modifiers*
  (construct build-cmd
             [[:? :h :help] help
              (str "When called with an argument displays help"
                   " information about that command. Otherwise, "
                   "displays a command list.")]
             ["->" store 
              (str "Stores the top value of the stack the register "
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
  (contains? *modifiers* (keyword m)))

(defn process-mod [m]
  (reset! *last-mod* (-> (keyword m) *modifiers* :cmd)))

(defn trigger-mod
  ([]
   (when @*last-mod*
     (@*last-mod*)
     (reset! *last-mod* nil)))
  ([sym]
   (@*last-mod* sym)
   (reset! *last-mod* nil)))
