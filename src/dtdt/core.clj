(ns dtdt.core
  (:import (java.util TimerTask Timer Date)))

(defn- create-task
  "internal function, extends abstract TimerTask class"
  ([f] (create-task f nil))
  ([f ex-handler] (let [f-ex (if ex-handler
                               (fn [] (try (f) (catch Throwable e (ex-handler e))))
                               f)]
                    (proxy
                      [TimerTask]
                      []
                      (run [] (f-ex))))))

(defn ^Timer create-timer
  "creates a new timer (and a corresponding thread) to run tasks"
  []
  (Timer.))


(def default-ex-handler (fn [^Throwable e]
                          (println (str "caught exception in dt-dt: " (.getMessage e)))))


(defprotocol ITimer
  "schedules future execution of tasks in the background"

  (every [this period-ms f] [this period-ms f initial-delay] [this period-ms f initial-delay ex-handler]
    "assigns a new task that will execute `f` every `period-ms` milliseconds.
    first execution will occur immediately unless `initial-delay` is provided.
    returns the created task")

  (in [this delay-ms f] [this delay-ms f ex-handler]
    "assigns a new task that will execute f once in `delay-ms` milliseconds
    returns the created task"))


(extend-type Timer
  ITimer
  (every
    ([t period-ms f] (every t period-ms f 0))
    ([t period-ms f initial-delay] (every t period-ms f initial-delay default-ex-handler))
    ([t period-ms f initial-delay ex-handler]
     (let [^TimerTask task (create-task f ex-handler)]
       (if (int? initial-delay)
         (. t scheduleAtFixedRate task ^long initial-delay ^long period-ms)
         (. t scheduleAtFixedRate task ^Date initial-delay ^long period-ms))
       task)))
  (in
    ([t delay-ms f] (in t delay-ms f default-ex-handler))
    ([t delay-ms f ex-handler]
     (let [task (create-task f ex-handler)]
       (. t schedule ^TimerTask task ^long delay-ms)
       task))))


(defn last-execution-time
  "returns last execution time of a task in epoch time"
  [^TimerTask task]
  (. task scheduledExecutionTime))

(defn cancel
  "cancels a task or a timer"
  [t]
  (condp instance? t
    TimerTask (. ^TimerTask t cancel)
    Timer (. ^Timer t cancel)))
