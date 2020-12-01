package main

import (
	"encoding/csv"
	"io"
	"log"
	"net/http"
	"os"
	"path"
	"path/filepath"
	"strconv"
	"strings"
	"sync"
	"time"

	"github.com/go-echarts/go-echarts/v2/charts"
	"github.com/go-echarts/go-echarts/v2/components"
	"github.com/go-echarts/go-echarts/v2/opts"
	"github.com/go-echarts/go-echarts/v2/types"
)

func loadData(sensor sensorType) ([]string, []opts.LineData) {
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	if err != nil {
		log.Println(err)
	}
	//Open file and write data into it
	file, err := os.Open(path.Join(dir, "data", sensor.sensorName+".csv"))
	if err != nil {
		log.Println(err)
	}
	defer file.Close()

	reader := csv.NewReader(file)

	times := make([]string, 0)
	values := make([]opts.LineData, 0)

	for {
		record, err := reader.Read()
		if err == io.EOF {
			break
		} else if err != nil {
			log.Fatal(err)
		}

		timestamp, err := strconv.ParseInt(record[0], 10, 64)
		if err != nil {
			log.Fatal(err)
		}

		value, err := strconv.ParseFloat(record[1], 64)
		if err != nil {
			log.Fatal(err)
		}

		times = append(times, time.Unix(timestamp, 0).Format("2006-01-02 15:04:05"))
		values = append(values, opts.LineData{Value: value})
	}

	return times, values
}

func generateChart(sensor sensorType) *charts.Line {
	times, values := loadData(sensor)

	line := charts.NewLine()

	var units string

	switch sensor.dataType {
	case temperature:
		units = "°C"
	case pressure:
		units = "hPa"
	case humidity:
		units = "%"
	}

	lastValue, _ := values[len(values)-1].Value.(float64)

	line.SetGlobalOptions(
		charts.WithInitializationOpts(opts.Initialization{
			Theme: types.ThemeVintage,
		}),
		charts.WithTitleOpts(opts.Title{
			Title:    strings.Title(string(sensor.dataType)),
			Subtitle: strings.Join([]string{"Latest ", string(sensor.dataType), ": ", strconv.FormatFloat(lastValue, 'f', 3, 64), units}, ""),
		}),
		charts.WithTooltipOpts(opts.Tooltip{
			Show: true,
		}),
		charts.WithDataZoomOpts(opts.DataZoom{
			Type:       "slider",
			Start:      50,
			End:        100,
			XAxisIndex: []int{0},
		}),
		charts.WithDataZoomOpts(opts.DataZoom{
			Type:       "inside",
			Start:      50,
			End:        100,
			XAxisIndex: []int{0},
		}),
		charts.WithYAxisOpts(opts.YAxis{
			AxisLabel: &opts.AxisLabel{Show: true, Formatter: "{value} " + units},
		}),
	)

	line.SetXAxis(times).
		AddSeries(strings.Title(string(sensor.dataType)), values).
		SetSeriesOptions(
			charts.WithLineChartOpts(opts.LineChart{
				Smooth: true,
			}),
		)

	return line
}

func httpserver(w http.ResponseWriter, _ *http.Request) {
	page := components.NewPage()
	page.SetLayout(components.PageFlexLayout)

	charts := make([]components.Charter, 0)
	for i := range sensorTypes {
		charts = append(charts, generateChart(sensorTypes[i]))
	}

	page.AddCharts(charts...)
	page.Render(w)
}

func showCharts(wg *sync.WaitGroup) {
	defer wg.Done()
	http.HandleFunc("/", httpserver)
	http.ListenAndServe(":8081", nil)
}
