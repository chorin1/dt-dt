(ns dtdt.timer-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [dtdt.core :as dt-dt]))

(def timer (atom (dt-dt/create-timer)))
(def counter (atom 0))

(defn- reset-timer [test-fn]
  (reset! timer (dt-dt/create-timer))
  (test-fn)
  (dt-dt/cancel @timer))

(defn- reset-counter [test-fn]
  (reset! counter 0)
  (test-fn))

(defn- inc-counter []
  (swap! counter inc))


(use-fixtures :each reset-timer reset-counter)

(deftest every-without-initial-delay
  (let [task (dt-dt/every 100 inc-counter @timer)]
    (Thread/sleep 150)
    (dt-dt/cancel task)
    (is (= 2 @counter))))

(deftest every-with-initial-delay
  (let [delayed-task (dt-dt/every 5 inc-counter @timer {:initial-delay 100})]
    (Thread/sleep 10)
    (dt-dt/cancel delayed-task)
    (is (zero? @counter))))

(deftest every-cancel-before-time
  (let [delayed-task (dt-dt/every 5000 inc-counter @timer)]
    (Thread/sleep 50)
    (dt-dt/cancel delayed-task)
    (is (= 1 @counter))))

(deftest in-executes-only-once
  (dt-dt/in 20 inc-counter @timer)
  (Thread/sleep 60)
  (is (= 1 @counter)))


(deftest every-exceptions
  (testing "throws exception on negative values"
    (is (thrown? IllegalArgumentException (dt-dt/every -1 inc-counter @timer)))
    (is (thrown? IllegalArgumentException (dt-dt/every 20 inc-counter @timer {:initial-delay -1})))
    (is (zero? @counter)))
  (testing "throws exception when using a canceled timer"
    (let [t2 (dt-dt/create-timer)]
      (dt-dt/cancel t2)
      (is (thrown? IllegalStateException (dt-dt/every 20 inc-counter t2)))
      (is (zero? @counter)))))
