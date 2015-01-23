(ns cljrpn.state
  "Defines the state object which determines the state of the program and provides simple tools to modify it")

(defn new-state [stack]
  "Creates a minimal state wrapping _stack_"
  {:stack stack})

(defmacro update-stack [state g & args]
  "Appies _g_ to the current stack and updates _state_ with generated stack"
  `(update-in ~state [:stack] ~g ~@args))

(defn pushf [state f]
  "Push _f_ to the top of the stack"
  (assoc state :stack (cons f (:stack state))))

(defn top
  "Peek at the top of the stack"
  ([state]
    (first (:stack state)))
  ([state n]
    (take n (:stack state))))

(defn popf
  "Removes the top element of the stack"
  ([state]
    (update-stack state rest))
  ([state n]
    (update-stack state (partial drop n))))

(defn stack-size
  "Returns the length of the stack."
  [state]
  (-> state :stack count))
