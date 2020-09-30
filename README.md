# Do this Do that
A simple, yet effective Clojure periodic task dispatcher.
<br><br>
## Why?
Inspired by the simplicity of [at-at](https://github.com/overtone/at-at) this library was created to address some of its issues:
* Dependency on clojure 1.3.0
* Reflection
* Silent error swallowing
* ScheduledThreadPoolExecutor which is suited for more heavy-duty tasks

Some of the other available Clojure libraries didn't quite fit, they either:
* Require java interop usage
* Depend on the core.async thread pool
* Bloatware
<br><br>
## Setup
Add the following to `project.clj` dependencies:
```
[dtdt "0.2.0"]
```

## Usage
Create a timer - each timer contains a single thread to run tasks.
```clojure
(def t (create-timer))
```
Next, assign a task to the timer. Task will start running periodically right away.
```clojure
(def some-task (every t 5000 #(println "Hello there..")))
=> #'dtdt.core/some-task
Hello there..
Hello there..
...
```

Assign as many tasks as you wish to a timer, make sure each function's execution is short to prevent hogging the Timer's thread.
<br><br>
Tasks are cancellable.
```clojure
Hello there..
Hello there..
(cancel some-task)
=> true
```
Notes:
* Timers can also be cancelled using the same `cancel` function.
* Canceling a timer will cancel all of its running tasks.

#### Initial delay
By default, new tasks will execute immediately. Optionally, you may pass an initial delay (or even a Date).
```clojure
; will execute f after 5 seconds and then every 10 milliseconds
(every t 10 f 5000)
```

#### last execution time
Return the last execution time of a task in epoch-time.
```clojure
(last-execution-time task)
=> 1601223864378
```

### One-off
Execute a task in X milliseconds from now.
```clojure
(in t 100 #(println "you will see me only once!"))
```


### Exceptions
When an exception from a task is thrown it will be printed.
```clojure
(def bad-task (every t 5000 #(throw (Exception. "Something is broken"))))
caught exception in dtdt: Something is broken
=> #'dtdt.core/bad-task1
caught exception in dtdt: Something is broken
caught exception in dtdt: Something is broken
```

Optionally, pass an exception handler to alter the default behaviour.
```clojure
(let [task #(throw (Exception. "Something broke again"))
      ex-handler (fn [e] (log/error (.getMessage e)))] 
  (every t 2000 task 0 ex-handler))
```

<br><br>

## License

Copyright © 2020<br>
Distributed under the Eclipse Public License
