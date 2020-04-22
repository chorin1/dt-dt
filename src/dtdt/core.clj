(ns dtdt.core
  (:import (java.util TimerTask Timer)))

(defn- create-task
  "Implements abstract TimerTask class"
  ([f] create-task f nil)
  ([f ex-handler] (let [f-ex (if ex-handler
                               (fn [] (try (f) (catch Exception e (ex-handler e))))
                               f)]
                    (proxy
                      [TimerTask]
                      []
                      (run [] (f-ex))))))

(defn create-timer
  "Creates a new timer (and a corresponding thread) to run tasks"
  []
  (Timer.))

(def default-ex-hadnler (fn [^Exception e]
                          (println (str "Caught exception in dtdt: " (.getMessage e)))))

(defn every
  "will create and run a new task that will execute every-ms milliseconds starting from initial-delay.
  returns the created task (can later be canceled)"
  [every-ms f timer & {:keys [initial-delay ex-handler]
                       :or   {initial-delay 0
                              ex-handler    default-ex-hadnler}}]

  (let [task (create-task f ex-handler)]
    (. ^Timer timer scheduleAtFixedRate ^TimerTask task ^long initial-delay ^long every-ms)
    task))

(defn in
  [delay-ms f timer {:keys [ex-handler]
                     :or   {ex-handler default-ex-hadnler}}]
  (let [task (create-task f ex-handler)]
    (. ^Timer timer schedule ^TimerTask task ^long delay-ms)
    task))

(defn last-execution-time
  "returns last execution time of the task in epoch time"
  [^TimerTask task]
  (. task scheduledExecutionTime))

(defn cancel
  "calling cancel with a timertask will cancel the task,
  calling it with a timer will cancel all timer's tasks"
  [t]
  (cond
    (instance? TimerTask t) (. ^TimerTask t cancel)
    (instance? Timer t) (. ^Timer t cancel)))
