package main

import (
	"sync"
)

type dataType string

const (
	temperature = "temperature"
	pressure    = "pressure"
	humidity    = "humidity"
)

type sensorType struct {
	sensorName string
	dataType   dataType
}

var sensorTypes []sensorType

func main() {
	sensorTypes = []sensorType{
		{"HTShumi", humidity},
		{"HTStemp", temperature},
		{"LPSpres", pressure},
		{"LPStemp", temperature}}

	var wg sync.WaitGroup

	wg.Add(1)
	go getAllSensorData(&wg)

	wg.Add(1)
	go showCharts(&wg)

	wg.Wait()
}
