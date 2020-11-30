GC   := go build
GENV := env GOOS=linux GOARCH=arm GOARM=7

main: main.go
	$(GENV) $(GC) -o $@ $^