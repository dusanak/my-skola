package main

import (
	"encoding/csv"
	"fmt"
	"io"
	"log"
	"os"
	"os/exec"
	"path"
	"path/filepath"
	"strconv"
	"strings"
	"sync"
	"time"
)

func getSensorData(sensor string) {
	//Call the C++ program to get data from sensor and save it to stdout
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	if err != nil {
		log.Println(err)
	}
	cmd := exec.Command(path.Join(dir, sensor))
	stdout, err := cmd.StdoutPipe()
	if err != nil {
		log.Println(err)
	}
	if err := cmd.Start(); err != nil {
		log.Println(err)
	}

	//Get data from stdout
	var data strings.Builder
	_, err = io.Copy(&data, stdout)
	if err != nil {
		log.Println(err)
	}
	dataString := data.String()[:len(data.String())-1]
	fmt.Println(dataString)

	//Open file and write data into it
	file, err := os.OpenFile(path.Join(dir, "data", sensor+".csv"), os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
	}
	time := time.Now().Unix()
	csvWriter := csv.NewWriter(file)
	if err := csvWriter.Write([]string{strconv.FormatInt(time, 10), dataString}); err != nil {
		log.Println(err)
	}
	csvWriter.Flush()
	if err := csvWriter.Error(); err != nil {
		log.Println(err)
	}
	file.Close()
}

func getAllSensorData(wg *sync.WaitGroup) {
	defer wg.Done()

	commands := []string{"HTShumi", "HTStemp", "LPSpres", "LPStemp"}
	ticker := time.NewTicker(10 * time.Second)

	for {
		select {
		case <-ticker.C:
			for _, v := range commands {
				go getSensorData(v)
			}
		}
	}
}

func main() {
	var wg sync.WaitGroup

	wg.Add(1)
	go getAllSensorData(&wg)

	wg.Wait()
}
