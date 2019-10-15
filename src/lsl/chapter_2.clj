(ns lsl.chapter-2)

;; note that we want to print the original expression
;; while printing on failure
;; how do we test this ?
;; (binding [*assert* false] (assert (= 1 2)))
(defmacro my-assert
  [expr]
  `(when *assert*
    (if-not ~expr
      (throw (new AssertionError
                  (str "Assert failed: " '~expr))))))

(binding [*assert* false]
  (macroexpand-1 '(my-assert (= 1 2))))

;; this does not throw an exception, as expected
(binding [*assert* false]
  (my-assert (= 1 2)))

;; but if i write my macro with the
;; "when *assert*" outside of the syntax quote
;; it does not work. why ?


(defmacro my-assert-bad
  [expr]
  (when *assert*
    `(if-not ~expr
       (throw (new AssertionError
                   (str "Assert failed: "
                        (pr-str '~expr)))))))

;; macroexpand-1 returns nil
;; which means the *assert* false took effect
(binding [*assert* false]
  (macroexpand-1 '(my-assert-bad (= 1 2))))

;; does macroexpand show something different ?
;; nope - still nil is the return value
;; NOTE: binding provides thread local bindings
(binding [*assert* false]
  (macroexpand '(my-assert-bad (= 1 2))))

;; but this still throws an exception
(binding [*assert* false]
  (my-assert-bad (= 1 2)))

(my-assert-bad (= 1 2))

;; the recommended way to set *assert* or
;; any compile time var is this
(set! *assert* true)

(macroexpand '(my-assert-bad (= 1 2)))
;; if i redefine *assert* as follows
;;(def ^:dynamic *assert* false)
;; before compiling my macro then the "when *assert*"
;; outside the syntax quote takes effect
;; the explanation is that macro body is "executed" during compile time
;; and the return value becomes the body to be replaced on macro usage

;; also *assert* is a compile time variable and should be set with set!
;; the recommended way is to set it in your project.clj the following
;; :global-vars {*warn-on-reflection* false
;;               *assert* false }


;; note that alter-var-root and binding operate at run time
;; hence they will not affect the compiled code if we use them
;; to set *assert* while creating a jar

;; if i write a macro like this
(def ^:dynamic x 5)
(defmacro hello
  []
  (eval '(+ x 2)))

;; the eval will be executed at macro compile time
;; so that my macro will always return 3
(macroexpand-1 '(hello))

(binding [x 7]
  (hello))


;; syntax quote namespace qualifies symbols
;; to avoid the symbol being overshadowed in the namespace it is used in


(defmacro squares
  [xs]
  `(map #(* % %)
        ~xs))

(defmacro squares-namespace-qualified-x
  [xs]
  `(map (fn [x]
          (* x x))
        ~xs))

;; throws error
;; (macroexpand-1 '(squares-namespace-qualified-x [1 2 3]))

(defmacro squares-non-qualified
  [xs]
  `(map (fn [~'x]
          (* ~'x ~'x))
        ~xs))

(macroexpand-1 '(squares-non-qualified (range 10)))


;; quoting 101
;; the quote or ' operator prevents evaluation
'(foo bar)

;; the syntax quote looks like this
`(1 2 3)

`(foo bar)
;; => (user/foo user/bar)
;; any symbols inside the syntax quote gets namespace qualified
;; this has implications for macro writing

;; the syntax quote comes with a escape hatch

(squares-non-qualified (range 10))


;; symbol capture ?
;; introducing new locals not specified by the user
(defmacro make-adder
  [x]
  `(fn [~'y] (+ ~'y ~x)))


(macroexpand-1 '(make-adder 10))

(let [y 400]
  ((make-adder (+ y y)) 10))

(macroexpand-1 '(make-adder (+ y y)))

(defmacro defaults
  [m]
  `(fn [~'m2]
     (merge ~m ~'m2)))

(macroexpand-1 '(defaults {:a 1}))

((defaults {:a 1}) {:b 2})

(let [m2 {:b 2}]
  ((defaults m2) {:c 3}))

(macroexpand-1 '(defaults m2))



(defn var-quote-example
  [f]
  (fn []
    (f)))

(defn two-plus-two
  []
  (+ 2))


(defn two-plus-two
  []
  (+ 2 2))

;; when i pass in a var quote it will always be looked up
(def x (var-quote-example #'two-plus-two))

(macroexpand-1 '(and true false true))

(macroexpand '(and true false true))

(clojure.walk/macroexpand-all '(and true false true))


;; symbol capture ? -continued
;; introducing new locals not specified by the user
(defmacro make-adder
  [x]
  (let [y (gensym)]
    `(fn [~y] (+ ~y ~x))))

;; another version with auto gensym notation
(defmacro make-adder
  [x]
  `(fn [y#] (+ y# ~x)))


(macroexpand-1 '(make-adder 10))

(let [y 400]
  ((make-adder (+ y y)) 10))

(macroexpand-1 '(make-adder (+ y y)))

;; always use gensym in let, letfn and try/catch bindings

(defmacro inspect-caller-locals
  [expr]
  (->> (keys &env)
       (map (fn [k] [~'k k]))
       (into {})))


(defmacro inspect-caller-locals
  [expr]
  (->> (keys &env)
       (map (fn [k] [`~'k k]))
       (into {})))

(let [foo "foo" bar "bar"]
  (inspect-caller-locals (+ 1 2)))

(clojure.walk/macroexpand-all '(let [foo "foo" bar "bar"]
                                 (inspect-caller-locals (+ 1 2))))

(macroexpand '(let [foo "foo" bar "bar"]
                (inspect-caller-locals (+ 1 2))))


(defmacro envv
  []
  &env)

(let [x  2]
  (envv))
