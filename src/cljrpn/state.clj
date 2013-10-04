(ns cljrpn.state
  "Defines the state object which determines the state of the program and provides simple tools to modify it")

(defn new-state [stack]
  "Creates a minimal state wrapping _stack_"
  {:stack stack})

(defn update-stack [state g]
  "Appies _g_ to the current stack and updates _state_ with generated stack"
  (update-in state [:stack] g))
              
(defn pushf [state f]
  "Push _f_ to the top of the stack"
  (assoc state :stack (cons f (:stack state))))

(defn top [state]
  "Peek at the top of the stack"
  (first (:stack state)))

(defn popf [state]
  "Removes the top element of the stack"
  (update-stack state rest))
