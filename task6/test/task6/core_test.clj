(ns task6.core-test
  (:require [clojure.test :refer :all]
            [task6.core :refer :all]))
(def test-g-1 (-> empty-map
                  (route "A" "B" 200 5)
                  (route "B" "C" 100 0)))

(def test-g-2 (-> empty-map
                  (route "A" "B" 123 2)
                  (route "D" "C" 321 1)
                  (route "C" "A" 9998 1)
                  (route "D" "A" 9998 0)))
(deftest a-test1
  (testing "simplify-graph1"
    (is (= 200 (get (get (simplify-graph test-g-1) "A") "B")))))
(deftest a-test2
  (testing "simplify-graph2-0-tickets"
    (is (= 9999 (get (get (simplify-graph test-g-1) "B") "C")))))
(deftest a-test3
  (testing "simplify-graph3"
    (is (= 123 (get (get (simplify-graph test-g-2) "A") "B")))))
(deftest a-test4
  (testing "simplify-graph4"
    (is (= 321 (get (get (simplify-graph test-g-2) "D") "C")))))
(deftest a-test5
  (testing "simplify-graph5"
    (is (= 9998 (get (get (simplify-graph test-g-2) "C") "A")))))
(deftest a-test6
  (testing "simplify-graph6-0-tickets"
    (is (= 9999 (get (get (simplify-graph test-g-2) "D") "A")))))