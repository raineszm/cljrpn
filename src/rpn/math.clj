(ns rpn.math)

(defn factorial [n]
  (loop [n n
         acc 1]
    (if (= n 1)
      acc
      (recur (dec n) (* acc n)))))
