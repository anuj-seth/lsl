(ns lsl.quoting-without-confusion)

(quote (+ 1 2 3))

(quote hi)

(quote (hi there))

(quote (hi there (my friends)))

(defn increment
  [x]
  (inc x))

(increment 0)

;; alter-var-root alters the root binding of the var
;; by applying the function to it's current value
;; so below it will actually be (comp increment increment)
(alter-var-root #'increment (partial comp increment))

(increment 0)


;; var is the actual container that holds a def'ed thing
;; the symbol is used to give it a name

`(apply + ~(map inc '(1 2 3)))

;; how can i make the above expression eval'able ?

;; doing this gives an error since 1 is not a function
(eval `(apply + ~(map inc '(1 2 3))))

;; i have to quote the result of the unquote
(eval `(apply + '~(map inc '(1 2 3))))

(eval '(apply + '(1 2 3)))

;; to avoid namespace qualification i could do this
;; since unquote form returning a symbol is not namespace qualified
`[:a ~(+ 1 2) ~(symbol "c")]

;; but this works better
;; the ~ will evaluate the 'c and return symbol c
;; which will remain non namespace qualified
`[:a ~(+ 1 2) ~'c]

`[:a ~(+ 1 2) ~`c]

;; in the second example the right most unquote
;; evaluates and returns 3 then the `~ cancel out
(= `[:a ~(+ 1 2) `~~(+ 1 2)]
   `[:a ~(+ 1 2) ~`~(+ 1 2)])



`{:a 1 :b '~(+ 1 2)}

;; to get a plain non namespaced quoted symbol
;; we can do
`{:a 1 :b '~'c}


;; wikibooks reader macros
;; #'foo 'foo @foo #^{:ack bar}  #"regex pattern"  #(inc %)
;; are all reader macros.
;; reader macros when encountered by the reader alter it's behaviour

;; syntax quote or backtick is also a reader macro

(defn rabbit [] 3)

`(moose ~(rabbit))

(def zebra [1 2 3])

`(moose ~zebra)

`(moose ~@zebra)

`(core/moose zebra#)

;; if syntax quote form is nested then the innermost syntax quoted
;; form is expanded first

`(moose ~(squirrel `(whale ~zebra)))

(def a 1)
;; when several ~ occur in a row the leftmost one belongs to the innermost syntax quote
`(zebra `~~a)
;; the inner `~ is evaluated which returns ~a and then outer ` is evaluated

(def a 5)
``(~~a)

;; a tilde matches a syntax quote if there are the same number of tildes as syntax quotes between them
;; in well formed expressions the outermost syntax quote matches the innermost tilde
(def x `a)
(def y `b)
(def a 1)
(def b 2)

``(w ~x ~~y)
;; we remove the outer most syntax quote and start evaluating things
;; w gets namespace qualified
;; ~x - the tilde has one more syntax quote than tilde between it and the backtick we just removed, hence we don't evaluate x but only
;; namespace qualify it
;; ~~y - the inner tilde has one tilde and one syntax quote between it and the backtick that we removed hence it get evaluated in surrounding context
`(user/w ~user/x ~user/b)
;; the repl will stop at above but if we evaluate further by callin eval we get below
(user/w user/a 2)

``(w ~x ~~@(list `a `b))

`(w ~user/x ~user/a ~user/b)
;; why does the repl stop at this point ?
