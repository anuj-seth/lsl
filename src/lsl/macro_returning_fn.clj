(ns lsl.macro-returning-fn
  (:require [clojure.string :as string]))

;; write a macro that returns a function
;; that behaves like (subs "abcd" 1 3)
(defmacro my-subs
  []
  `(fn [line start end]
     (subs line start end)))

;; this throws an error and macroexpand-1 shows why
;;((my-subs) "abcd" 1 3)
;;(macroexpand-1 '(my-subs))

;; we could make those symbols non-namespace qualified
(defmacro my-subs-2
  []
  `(fn [~'line ~'start ~'end]
     (subs ~'line ~'start ~'end)))

(clojure.walk/macroexpand-all '((my-subs-2) "abcd" 1 3))
((my-subs-2) "abcd" 1 3)

(let [line "xyz"]
  ((my-subs-2) "abcd" 1 3))

;; or i could use auto-gensyms
(defmacro my-subs-3
  []
  `(fn [line# start# end#]
     (subs line# start# end#)))

(clojure.walk/macroexpand-all '((my-subs-3) "abcd" 1 3))
((my-subs-3) "abcd" 1 3)

;; write a macro that takes a string as input
;; and returns a function of one string arg that joins the two
(defmacro join-join
  [s]
  `(fn [l#]
     (str s l#)))

;; can you spot the problem in the expansion ?
(macroexpand-1 '(join-join "x"))
;;((join-join "x") "y")


(defmacro join-join-2
  [s]
  `(fn [l#]
     (str ~s l#)))

(macroexpand-1 '(join-join-2 "x"))
((join-join-2 "x") "y")


;; write a macro that takes a even number of args
;; and returns a hash map after keyword'izing pairs
(defmacro make-map
  [& kvs]
  `(hash-map :type :hello
             ~@(map (fn [[k v]]
                      [(keyword k) (keyword v)])
                    (partition 2 kvs))))

(macroexpand-1 '(make-map "x" "1"))

;; does not work because the return of the quote
;; is a list which it tries to execute as a function
;; ([:x :1])
;; Wrong number of args (0) passed to: clojure.lang.PersistentVector
(defmacro make-map-2-not-working
  [& kvs]
  `(into {}
         ~(map (fn [[k v]]
                  [(keyword k) (keyword v)])
                (partition 2 kvs))))


(defmacro make-map-2
  [& kvs]
  `(into {}
    '~(map (fn [[k v]]
             [(keyword k) (keyword v)])
           (partition 2 kvs))))

(macroexpand-1 '(make-map-2 "x" "1"))

(make-map-2 "x" "1")

;; the last unquote splice is just to make the map literal reader
;; happy that there are even number of forms
(defmacro make-map-3
  [& kvs]
  `{:type :hello
    ~@(mapcat (fn [[k v]]
                [(keyword k) (keyword v)])
              (partition 2 kvs))
    ~@[]})


(macroexpand-1 '(make-map-3 "x" "1"))

(make-map-3 "x" "1")

;; a macro is run and it's resulting value
;; is evaluated in the context of the calling location
;; if you try to run the id-type macro, the max will
;; get resolved at the caller but not a b c
(defmacro id-type
  [f a b c]
  ;;[f a b c]
  '(f a b c))

(macroexpand-1 '(id-type max a b c))
(let [f min a 1 b 2 c 3]
  (id-type max a b c))

(defmacro id
  [f a b c]
  `(~f ~a ~b ~c))

(macroexpand-1 '(id max 1 2 3))
(id max 1 2 3)





