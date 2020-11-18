(ns tasks.core)

(defn heavy-even [num]
  (Thread/sleep 5)
  (even? num))

(defn p-filter-finite
  ([pred coll]
   (let [chunk-size (int (Math/ceil (Math/sqrt (count coll)))),
      parts (partition-all chunk-size coll)]
   (->> parts
      (map (fn [coll1]
        (future (doall (filter pred coll1)))))
      (doall)
      (map deref)
      (flatten)))))

(defn p-filter
  ([pred coll]
   (if (empty? coll)
     '()
     (concat (p-filter-finite pred (take 1000 coll))
             (lazy-seq (p-filter pred (drop 1000 coll)))))))


(use 'clojure.test)
(deftest eg-tests
  (is (= (reduce + (p-filter even? (range 0 50))) (reduce + (filter even? (range 0 50)))))
  (is (= (reduce + (p-filter odd? (range 0 58))) (reduce + (filter odd? (range 0 58)))))
  (is (= (reduce + (p-filter even? (range 8 90))) (reduce + (filter even? (range 8 90)))))
  (is (= (reduce * (p-filter even? (range 0 10))) (reduce * (filter even? (range 0 10)))))
  (is (= (reduce + (take 1000 (p-filter even? (range)))) (reduce + (take 1000 (filter even? (range))))))
  (is (= (reduce + (take 1100 (p-filter even? (range)))) (reduce + (take 1100 (filter even? (range)))))))

(run-tests)

(time (->> (range 100)
           (p-filter heavy-even)
           (reduce +)))
(time (->> (range 100)
           (filter heavy-even)
           (reduce +)))


(time (reduce + (take 1100 (p-filter heavy-even (range)))))
(time (reduce + (take 1100 (filter heavy-even (range)))))



