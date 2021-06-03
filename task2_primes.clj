
;;Добавление след. числа после n в решето
(defn add-to-sieve [sieve n step]
  (let [m (+ n step)]
    (if (sieve m)
      (recur sieve m step)
      (assoc sieve m step))))

;;Проверка числа n на простоту (лежит ли оно в решете),
;;если оно в решете, то достаем его оттуда. Затем добавляем в решето следущее за ним
(defn next-sieve [sieve n]
  (if-let [step (sieve n)]
    (-> (dissoc sieve n)
      (add-to-sieve n step))
    (add-to-sieve sieve n (+ n n))))

;;Основная функция
(defn next-primes [sieve n]
  (if (sieve n)
    (next-primes (next-sieve sieve n) (+ 2 n))
    (cons n 
      (lazy-seq (next-primes (next-sieve sieve n) (+ 2 n))))))

;;Последовательность
(defn primes []
  (concat [2] (next-primes {} 3)))

  
(use 'clojure.test)
(deftest eg-tests
  (is (= (last (take 2 (primes))) 3))
  (is (= (last (take 4 (primes))) 7))
  (is (= (last (take 5 (primes))) 11)))

(run-tests)
