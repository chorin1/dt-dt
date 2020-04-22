(defproject dtdt "0.1"
  :description "Do this Do that - A clojure function scheduler"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v20.html"}
  :dependencies []
  :main ^:skip-aot dtdt.core
  :profiles {:uberjar   {:aot :all}
             :dev       {:dependencies [[org.clojure/clojure "1.10.1"]]
                         :global-vars    {*warn-on-reflection* true}}})
