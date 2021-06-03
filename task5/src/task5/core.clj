(def t-restarts (atom 0))

(defn philosopher [times dining-time thinking-time n forks]
  (let [f1 (nth forks n), f2 (nth forks (mod (inc n) (count forks)))]
    (dotimes [i times]
      (do
        (Thread/sleep thinking-time)
        (dosync
          (swap! t-restarts inc)
          (alter f1 inc)
          (locking *out*
            (println n "with fork" n))
          (alter f2 inc)
          (locking *out*
            (println n "with fork" (mod (inc n) (count forks))))
            (Thread/sleep dining-time))))))

(defn run-phil [n times eat-time think-time]
  (let [forks (map #(ref %) (repeat n 0)),
        phils (map #(future (philosopher times eat-time think-time % forks)) (range n))]
    (map deref phils)))

(def n 9)
(def cycles 10)

(time (println (run-phil n cycles 100 200)))
(println "Restarts count:" (- @t-restarts (* n cycles)))

;(defn philosopher [times dining-time thinking-time n forks]
;  (let [f1 (nth forks n), f2 (nth forks (mod (inc n) (count forks)))]
;    (dotimes [i times]
;      (do
;        (Thread/sleep thinking-time)
;        (dosync
;          (swap! t-restarts inc)
;          (commute f1 inc)
;          (locking *out*
;            (println n "with fork" n))
;          (commute f2 inc)
;          (locking *out*
;            (println n "with fork" (mod (inc n) (count forks))))
;          (Thread/sleep dining-time))))))
;
;(defn run-phil [n times eat-time think-time]
;  (let [forks (map #(ref %) (repeat n 0)),
;        phils (map #(future (philosopher times eat-time think-time % forks)) (range n))]
;    (map deref phils)))
;
;(def n 9)
;(def cycles 10)
;
;(time (println (run-phil n cycles 100 200)))
;(println "Restarts count:" (- @t-restarts (* n cycles)))