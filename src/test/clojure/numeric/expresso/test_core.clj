(ns numeric.expresso.test-core
  (:use numeric.expresso.core)
  (:use clojure.test)
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic.protocols]
        [clojure.core.logic :exclude [is] :as l]
        clojure.test)
  (:require [clojure.core.logic.fd :as fd])
  (:require [clojure.core.logic.unifier :as u]))

(deftest test-simplify
  (is (= 4 (simplify (ex (+ 2 2)))))
  (is (= 137 (simplify (ex (+ (* 5 20) 30 7)))))
  (is (= 0 (simplify (ex (- (* 5 x) (* (+ 4 1) x))))))
  (is (= 0 (simplify (ex (* (/ y z) (- (* 5 x) (* (+ 4 1) x)))))))
  (is (= '(clojure.core/* 6 x) (simplify (ex (* 3 2 x)))))
  (is (= '(clojure.core/* 720 x y z) (simplify (ex (* 2 x 3 y 4 z 5 6)))))
  (is (= 7 (simplify (ex (+ x 3 4 (- x)))))))

(deftest test-transform-to-polynomial-normal-form
  (is (= '(numeric.expresso.core/** x 3)
         (to-polynomial-normal-form 'x (ex (+ (** x 3) (* 3 (** x 2))
                                              (- (* 2 (** x 2))
                                                 (* 5 (** x 2))))) )))
  (is (= '(clojure.core/+ (clojure.core/* 243.0 (numeric.expresso.core/** x 10)) (clojure.core/* 1215.0 (numeric.expresso.core/** x 9)) (clojure.core/* 4050.0 (numeric.expresso.core/** x 8)) (clojure.core/* 8910.0 (numeric.expresso.core/** x 7)) (clojure.core/* 15255.0 (numeric.expresso.core/** x 6)) (clojure.core/* 19683.0 (numeric.expresso.core/** x 5)) (clojure.core/* 20340.0 (numeric.expresso.core/** x 4)) (clojure.core/* 15840.0 (numeric.expresso.core/** x 3)) (clojure.core/* 9600.0 (numeric.expresso.core/** x 2)) (clojure.core/* 3840.0 x) 1024.0)
         (to-polynomial-normal-form 'x (ex (** (+ (* 3 x) 4 (* 3 (** x 2))) 5))))))

(deftest test-rearrange
  (is (= '(clojure.core/= x (clojure.core/- 4 1))
         (rearrange 'x (ex (= (+ 1 x) 4))))))


(deftest test-solve
  (is (= '(clojure.core/= x 2) (solve 'x (ex (= (+ 1 x) 3)))))
  (is (= '() (solve 'x (ex (= x (+ x 1))))))
  (is (= '_0 (solve 'x (ex (= x x)))))
  (is (= (ex (= x -4))
         (solve 'x (ex (= (* 3 x) (+ (* 4 x) 4)))))))

(deftest test-differentiate
  (is (= (ex (* 2 x)) (differentiate '[x] (ex (** x 2)))))
  (is (= 2 (differentiate '[x x] (ex (** x 2)))))
  (is (= (ex (* 3 (** x 2))) (differentiate '[x] (ex (** x 3)))))
  (is (= '(clojure.core/+ (clojure.core/* 3 (numeric.expresso.core/** x 3)) (clojure.core/* 9 (numeric.expresso.core/** x 2) x))
         (differentiate '[x] (ex (* (** x 3) (* 3 x)))))))

(deftest test-compile-expr
  (is (= 4 ((compile-expr (ex (+ (* 1 2) (* 2 1)))) {})))
  (is (= 8 ((compile-expr (ex (+ (* x 2) (* 2 x)))) {'x 2}))))

(deftest test-optimize
  (is (= 4 ((optimize (ex (+ (* 1 2) (* 2 1)))) {})))
  (is (= 8 ((optimize (ex (+ (* x 2) (* 2 x)))) {'x 2}))))

