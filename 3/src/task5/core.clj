(def fork1 (ref 0))
(def fork2 (ref 0))
(def fork3 (ref 0))
(def fork4 (ref 0))
(def fork5 (ref 0))

(def forks [fork1, fork2, fork3, fork4, fork5])

(defn philosopher [times dining-time thinking-time]
  (let [f1 (rand-nth forks), new-forks (remove #{f1} forks), f2 (rand-nth new-forks)]
    (dotimes [n times]
      (dosync
        (Thread/sleep dining-time)
        (locking *out*
          (println (.getName (Thread/currentThread)))
          (println "with fork" f1)
          (println "with fork" f2)
          )
        (alter f1 inc)
        (alter f2 inc)
        (Thread/sleep thinking-time)
        )
      )
    )
  )

(defn runs [philosophers times eat-time think-time]
  (->> (dotimes [n philosophers] (future (philosophers times eat-time think-time)))
      (doall)
      (map deref))
  )


(runs 2 5 100 200)