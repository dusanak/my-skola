GC   := go build
GENV := env GOOS=linux GOARCH=arm GOARM=7

main: main.go sensor_polling.go charting.go
	$(GENV) $(GC) -o $@ $^