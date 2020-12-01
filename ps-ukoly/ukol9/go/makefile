GC   	 := go build
GENV 	 := env GOOS=linux GOARCH=arm GOARM=7

GSOURCES := src

main: $(GSOURCES)/main.go $(GSOURCES)/sensor_data.go $(GSOURCES)/charts.go
	$(GENV) $(GC) -o $@ $^