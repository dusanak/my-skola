package main

import (
	"fmt"
	"io"
	"log"
	"os"
	"os/exec"
	"path"
	"path/filepath"
	"strings"
	"sync"
)

func getSensorData(wg *sync.WaitGroup) {
	defer wg.Done()

	commands := []string{"HTShumi", "HTStemp", "LPSpres", "LPStemp"}

	for _, v := range commands {
		dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
		if err != nil {
			log.Println(err)
		}

		cmd := exec.Command(path.Join(dir, v))

		stdout, err := cmd.StdoutPipe()
		if err != nil {
			log.Println(err)
		}

		if err := cmd.Start(); err != nil {
			log.Println(err)
		}

		var buf strings.Builder
		_, err = io.Copy(&buf, stdout)

		if err != nil {
			log.Println(err)
		}

		fmt.Println(buf.String())
	}
}

func main() {
	var wg sync.WaitGroup

	wg.Add(1)
	go getSensorData(&wg)

	wg.Wait()
}
