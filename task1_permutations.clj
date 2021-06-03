(defn connect-str [x xs]
  (map 
    (fn [y] 
      (str x y))
    xs))

(defn my-flatten [xs]
  (reduce concat xs))

(defn remove-letters [s xs]
  (reduce (fn [ys z]
    (remove (fn [a] (= a (str z))) ys)) xs s))

(defn permute-helper [xs perms]
  (my-flatten (map 
    (fn [y] 
      (connect-str y 
        (remove-letters y xs)))
    perms)))

(defn n-times [f n acc]
  (reduce (fn [a b] (f a)) acc (range n)))

(defn permutation [xs n]
  (n-times (fn [ys] (permute-helper xs ys)) (dec n) xs))

(permutation ["a" "b" "c" "d"] 4)