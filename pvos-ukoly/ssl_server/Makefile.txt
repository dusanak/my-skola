TARGET1=ssl_cli
TARGET2=ssl_srv
TARGETS=$(TARGET1) $(TARGET2)

all: $(TARGETS)

$(TARGET1): $(TARGET1).cpp
	g++ -std=c++11 -pthread $^ -o $@ -lssl -lcrypto
	
$(TARGET2): $(TARGET2).cpp
	g++ -std=c++11 -pthread $^ -o $@ -lssl -lcrypto
	
clean: 
	rm -rf $(TARGETS)