(ns task6.core)

;;;an empty route map
;;;it is enough to use either forward or backward part (they correspond to each other including shared reference to number of tickets)
;;;:forward is a map with route start point names as keys and nested map as values
;;;each nested map has route end point names as keys and route descriptor as values
;;;each route descriptor is a map (structure in fact) of the fixed structure where
;;;:price contains ticket price
;;;and :tickets contains reference to tickets number
;;;:backward has the same structure but start and end points are reverted
(def empty-map
  {:forward  {},
   :backward {}})

(defn route
  "Add a new route (route) to the given route map
   route-map - route map to modify
   from - name (string) of the start point of the route
   to - name (string) of the end point of the route
   price - ticket price
   tickets-num - number of tickets available"
  [route-map from to price tickets-num]
  (let [tickets (ref tickets-num :validator (fn [state] (>= state 0))), ;reference for the number of tickets
        orig-source-desc (or (get-in route-map [:forward from]) {}),
        orig-reverse-dest-desc (or (get-in route-map [:backward to]) {}),
        route-desc {:price   price,                         ;route descriptor
                    :tickets tickets},
        source-desc (assoc orig-source-desc to route-desc),
        reverse-dest-desc (assoc orig-reverse-dest-desc from route-desc)]
    (-> route-map
        (assoc-in [:forward from] source-desc)
        (assoc-in [:backward to] reverse-dest-desc))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Dijkstra
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def inf 999999)

(defn dijkstra [g src dst]
  (loop [dists (assoc (zipmap (keys g) (repeat inf)) src 0)
         curr src
         ns (keys (get g curr))
         unvisited (apply hash-set (keys g))
         p (zipmap (keys g) (repeat nil))]
    (if (empty? unvisited)
      [(get dists dst),
       (loop [path []
              t dst]
         (if (= t src)
           (cons src path)
           (recur (cons t path) (get p t))))]
      (if (empty? ns)
        (let [u (disj unvisited curr)
              next-n (first (sort-by #(get dists %) u))]
          (recur dists next-n (filter #(some (fn [s] (= s %)) u) (keys (get g next-n))) u p))
        (let [cdst (get dists curr)
              idist (get dists (first ns))
              sum (+ cdst (get (get g curr) (first ns)))
              result (if (< sum idist)
                       (assoc dists (first ns) sum)
                       dists)
              path (if (< sum idist)
                     (assoc p (first ns) curr)
                     p)]
          (recur result curr (rest ns) unvisited path))))))

(defn simplify-graph [graph]
  (let [g (get graph :forward)]
    (zipmap
      (keys g)
      (map #(zipmap
              (keys %)
              (map
                (fn [param1] (if
                               (> (deref (param1 :tickets)) 0)
                               (get param1 :price)
                               9999)) (vals %))) (vals g)))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn book-tickets
  "Tries to book tickets and decrement appropriate references in route-map atomically
   returns map with either :price (for the whole route) and :path (a list of destination names) keys
          or with :error key that indicates that booking is impossible due to lack of tickets"
  [route-map from to]
  (if (= from to)
    {:path '(), :price 0}
    (let [graph (simplify-graph route-map)
          path (dijkstra graph from to)]
        (try
          (dosync
            (loop [p (second path)]
              (if (< (count p) 3)
                nil
                (let [r (:tickets (get (get (:forward route-map) (first p)) (second p)))]
                  (do
                    (alter r dec)
                    (recur (next p)))))))
          {:path (second path), :price (first path)}
          (catch Exception e {:error e})))))


;;;cities
(def spec1 (-> empty-map
               (route "City1" "Capital" 200 5)
               (route "Capital" "City1" 250 5)
               (route "City2" "Capital" 200 5)
               (route "Capital" "City2" 250 5)
               (route "City3" "Capital" 300 3)
               (route "Capital" "City3" 400 3)
               (route "City1" "Town1_X" 50 2)
               (route "Town1_X" "City1" 150 2)
               (route "Town1_X" "TownX_2" 50 2)
               (route "TownX_2" "Town1_X" 150 2)
               (route "Town1_X" "TownX_2" 50 2)
               (route "TownX_2" "City2" 50 3)
               (route "City2" "TownX_2" 150 3)
               (route "City2" "Town2_3" 50 2)
               (route "Town2_3" "City2" 150 2)
               (route "Town2_3" "City3" 50 3)
               (route "City3" "Town2_3" 150 2)))

(defn booking-future [route-map from to init-delay loop-delay]
  (future
    (Thread/sleep init-delay)
    (loop [bookings []]
      (Thread/sleep loop-delay)
      (let [booking (book-tickets route-map from to)]
        (if (booking :error)
          bookings
          (recur (conj bookings booking)))))))

(defn print-bookings [name ft]
  (println (str name ":") (count ft) "bookings")
  (doseq [booking ft]
    (println "price:" (booking :price) "path:" (booking :path))))

(defn run []
  ;;try to tune timeouts in order to all the customers gain at least one booking
  (let [f1 (booking-future spec1 "City1" "City3" 99 1),
        f2 (booking-future spec1 "City1" "City2" 100 1),
        f3 (booking-future spec1 "City2" "City3" 100 1)]
    (print-bookings "City1->City3:" @f1)
    (print-bookings "City1->City2:" @f2)
    (print-bookings "City2->City3:" @f3)

    ))

(run)
