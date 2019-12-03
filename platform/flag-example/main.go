package main

import (
	"fmt"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"log"
	"net/http"
	"time"

	"github.com/prometheus/client_golang/prometheus/promhttp"
)

func recordMetrics() {
	go func() {
		for {
			opsProcessed.Inc()
			if flip {
				downstreamDependency.Set(1)
				flip = false
			} else {
				downstreamDependency.Set(0)
				flip = true
			}
			time.Sleep(1 * time.Second)
		}
	}()
}

var (
	opsProcessed = promauto.NewCounter(prometheus.CounterOpts{
		Name: "flag_example_processed_ops_total",
		Help: "The total number of processed events",
	})

	flip = true

	downstreamDependency = promauto.NewGauge(prometheus.GaugeOpts{
		Name:        "downstream_dependency",
		Help:        "Downstream dependency of a made up dependency",
		ConstLabels: prometheus.Labels{"dependency_name": "alternate"},
	})

	featureFlag = promauto.NewGauge(prometheus.GaugeOpts{
		Name:        "feature_flag",
		Help:        "Feature flag which gets triggered on an endpoint call",
		ConstLabels: prometheus.Labels{"feature_flag_name": "endpoint"}})
)

func main() {
	println("Flag example starting up!")
	downstreamDependency.Set(0)
	featureFlagTracker := float64(0)
	featureFlag.Set(0)
	recordMetrics()

	// Prometheus metrics handler
	http.Handle("/metrics", promhttp.Handler())

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		featureFlag.Set(nextFeatureFlagValue(&featureFlagTracker))
		_, _ = fmt.Fprint(w, "Hello, world! feature_flag has been changed.")
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}

func nextFeatureFlagValue(featureFlagTracker *float64) float64 {
	switch *featureFlagTracker {
	case 0:
		*featureFlagTracker = 1
	case 1:
		*featureFlagTracker = 2
	case 2:
		*featureFlagTracker = -1
	default:
		*featureFlagTracker = 0
	}
	return *featureFlagTracker
}
