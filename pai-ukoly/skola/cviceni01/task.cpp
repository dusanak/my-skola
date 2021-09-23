#include <iostream>
#include <cstring>
#include <cstdio>
#include <string>
#include <thread>
#include <future>
#include <chrono>
#include <random>
#include <vector>
#include <algorithm>
#include <utility>

#include "cClientSocket.hpp"

#define THREADS 8
#define REQUESTS_PER_THREAD 16
#define HOST "name-service.appspot.com"
#define ADDR "/api/v1/names/"

std::string getUser(int user_number) {
    std::string host = HOST;
    std::string addr = ADDR;

    arg::cClientSocket client;
	if (!client.OpenClientSocket(host, 80)) {
		std::cout << "Not connected!" << std::endl;
		return "";
	}

    std::string filename = std::to_string(user_number) + ".xml";
    std::string get_http = "GET " + addr + filename + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n";

    client.Send(get_http);
	std::string response = client.Receive();

    client.Close();

    return response;
}

std::vector<std::string> getUsers(int start_user, int number_of_users) {
    std::vector<std::string> users;

    for (int i = start_user; i < start_user + number_of_users; i++) {
        users.push_back(getUser(i));
    }

    return users;
}

int main() {
	//URL: http://name-service.appspot.com/api/v1/names/0.xml

    std::vector<std::future<std::vector<std::string>>> my_futures;

    for (int i = 0; i < THREADS; i++) {
        my_futures.push_back(async(std::launch::async, getUsers, i, i * THREADS));
    }

    std::vector<std::string> users;

    for (int i = 0; i < my_futures.size(); i++) {
        for (auto user: my_futures[i].get()) {
            users.insert(users.end(), users.begin(), users.end());
        }
    }
	
	return 0;
}
