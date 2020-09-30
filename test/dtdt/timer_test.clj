(ns dtdt.timer-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [dtdt.core :refer [create-timer cancel every in]]))

(def timer (atom (create-timer)))
(def counter (atom 0))

(defn- reset-timer [test-fn]
  (reset! timer (create-timer))
  (test-fn)
  (cancel @timer))

(defn- reset-counter [test-fn]
  (reset! counter 0)
  (test-fn))

(defn- inc-counter []
  (swap! counter inc))


(use-fixtures :each reset-timer reset-counter)

(deftest every-without-initial-delay
  (let [task (every @timer 100 inc-counter)]
    (Thread/sleep 150)
    (cancel task)
    (is (= 2 @counter))))

(deftest every-with-initial-delay
  (let [delayed-task (every @timer 5 inc-counter 100)]
    (Thread/sleep 10)
    (cancel delayed-task)
    (is (zero? @counter))))

(deftest every-cancel-before-time
  (let [delayed-task (every @timer 5000 inc-counter)]
    (Thread/sleep 50)
    (cancel delayed-task)
    (is (= 1 @counter))))

(deftest in-executes-only-once
  (in @timer 20 inc-counter)
  (Thread/sleep 60)
  (is (= 1 @counter)))


(deftest every-exceptions
  (testing "throws exception on negative values"
    (is (thrown? IllegalArgumentException (every @timer -1 inc-counter)))
    (is (thrown? IllegalArgumentException (every @timer 20 inc-counter -1)))
    (is (zero? @counter)))
  (testing "throws exception when using a canceled timer"
    (let [t2 (create-timer)]
      (cancel t2)
      (is (thrown? IllegalStateException (every t2 20 inc-counter)))
      (is (zero? @counter)))))
