(ns cljrpn.state)

(defrecord State [stack])

(defn update-stack [state g]
  (update-in state [:stack] g))
              
(defn pushf [state f]
  (assoc state :stack (cons f (:stack state))))

(defn top [state]
  (first (:stack state)))

(defn popf [state]
  (update-stack state rest))
