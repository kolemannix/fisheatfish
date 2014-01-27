(ns fisheatfish.tank)

(def ^{:private true} refresh-interval 1000) ; 1/10 second
(def id-counter (atom 0))
(def cycle-counter (atom 0))
(def max-cycles 5)
(reset! id-counter 0)
(defn- next-id []
  (swap! id-counter inc))

(defn- peek-id [] @id-counter)

(defn- random-position [{[x-max y-max] :size}]
  [(* x-max (.nextDouble (java.util.Random.)))
   (* y-max (.nextDouble (java.util.Random.)))])

(defn- random-aggression [] (.nextDouble (java.util.Random.)))

(defrecord Fish [id aggression position]
  Object
  (toString [this] (str "Fish " id ": " position))
  )

(defrecord Tank [size fish-list])

(defn behave "Main update function for a fish"
  [fish tank]
  ;; (println (str "I am fish number " (:id fish) " and I am behaving"))
  (let [[old-x old-y] (:position fish)
        new-fish (assoc fish :position [(double (+ old-x 2.0)) (double (+ old-y 2.0))])]
    (do
      (println "old fish\n" (:position fish))
      (println "new fish\n" (:position new-fish))
      new-fish))
  )
(defn- add-fish
  ([tank] (add-fish (random-aggression) (random-position tank) tank))
  ([aggression position tank]
     (assoc tank :fish-list (conj (:fish-list tank) (->Fish (next-id) aggression position)))))

(defn create-tank 
  "Creates a fish tank agent"
  ([size]
     (let [new-tank (->Tank size (list))]
       new-tank)))

(defn test-tank "Returns a pre-populated tank for testing"
  [size]
  (-> (create-tank size)
      (add-fish)
      (add-fish)
      (add-fish)
      (add-fish)))

(defn- do-cycle
  "Brings the tank to its next state"
  [tank]
  (do (swap! cycle-counter inc))
  (let [fish-behave (fn [fish] (behave fish tank))
        new-fish-list (doall (map fish-behave (:fish-list tank)))]
    (do
      ;; (println "new fish list\n")
      (assoc tank :fish-list new-fish-list))))

(defn- simulation-loop [t callback-fn]
  (loop [tank t]
    (if (> @cycle-counter max-cycles)
      (println "exiting after" max-cycles "cycles")
      (do
        (Thread/sleep refresh-interval)
        (println "doing cycle " @cycle-counter)
        (recur (-> tank
                   do-cycle
                   callback-fn)))))
  )

(defn reset-all []
  (reset! id-counter 0))

(defn begin [t callback]
  "Starts the simulation. Callback is the desired refresh function. Callback should
   expect to receive a FishTank as an argument"
  (do
    (reset! id-counter 0)
    (reset! cycle-counter 0)
    (simulation-loop t callback)))
