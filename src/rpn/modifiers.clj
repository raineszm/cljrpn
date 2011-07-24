(ns rpn.modifiers
  (:use [rpn.operators :only [*operators* operator?]]
        [rpn.commands :only [*cmds* cmd? build-cmd]]
        [rpn.numbers :only [hex? binary? with-base]]
        [rpn.stack :only [pushf]]
        [rpn.utils]
        [clojure.string :only [replace-first]]))


(declare *modifiers*)

(defn- prep-keys [k]
  (map #(replace-first (str %) ":" "") k))

(defn print-help [table sym]
  (let [op ((keyword sym) table)
        aliases (:cmds op)
        effect (:help op)]
    (apply println "Aliases: "(prep-keys aliases))
    (println effect))
  (println))

(declare modifier?)

(defn help
  ([]
   (do
     (print "Operators: ")
     (apply println (prep-keys (keys *operators*)))
     (println)
     (print "Commands: ")
     (apply println (prep-keys (keys *cmds*)))
     (println)
     (print "Modifiers: ")
     (apply println (prep-keys (keys *modifiers*)))))
  ([sym]
   (cond
     (operator? sym) (print-help *operators* sym)
     (cmd? sym) (print-help *cmds* sym)
     (modifier? sym) (print-help *modifiers* sym))))

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
