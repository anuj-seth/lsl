(ns lsl.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))




;; #_(defmapping service-call "SVCL"
;;     (04 18 customer-name)
;;     (19 23 customer-id)
;;     (24 27 call-type-code)
;;     (28 35 date-of-call-string))

;; #_(defmapping usage "USGE"
;;     (04 08 customer-id)
;;     (09 22 customer-name)
;;     (30 30 cycle)
;;     (31 36 read-date))



;; shared state does not play well with lazy sequences
(def some-shared-state (atom 0))

(defn manipulate
  []
  (swap! some-shared-state inc)
  (map (fn [x]
         (println "shared state" @some-shared-state)
         (+ @some-shared-state x))
       [1 1 1]))




(def x (manipulate))

(def y (manipulate))

(println x)

(println y)
