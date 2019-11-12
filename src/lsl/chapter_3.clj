(ns lsl.chapter-3
  (:require [clojure.string :as string]))

;; macros are not values

(defmacro square
  [x]
  `(* ~x ~x))

;;(macroexpand-1 '(square x))
;; you cannot use a macro here since it is done with by the time
;; the map is to be called
;;(map square [1 2 3])

(defmacro only-name
  [expr]
  `(string/join " " '~(rest expr)))

(macroexpand-1 '(only-name ["Mr." "Arnold" "Schwarzenegger"]))

(map (fn [x]
       (only-name x))
     [["Mr." "A" "B"]
      ["Miss" "X" "Y"]])
;; don't know how to create ISeq from symbol
;; macros are compile time and they must get `code`
;; at compile time that resolves to a list in this case
;; the type of x
;; you can get the same error from

(rest `zz-top)

(only-name ["Mr." "Arnold" "Schwarzenegger"])

(string/join "ab" "cd")


;; macros tend to infect code
