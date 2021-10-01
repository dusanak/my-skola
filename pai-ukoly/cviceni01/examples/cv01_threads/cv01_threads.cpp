// cv01_threads.cpp: Definuje vstupní bod pro konzolovou aplikaci.
//

#include "stdafx.h"
#include <iostream>

#include <thread>
#include <future>
#include <chrono>
#include <random>

#include <vector>
#include <algorithm>
#include <utility>

using namespace std;

typedef vector<double> t_point;

/**
* Create a PRNG for every thread.
* TODO: make a safe initialization, use this_thread::get_id()
*/
double drand() {
	static thread_local mt19937 generator;
	uniform_int_distribution<unsigned int> distribution(0, 10);
	return distribution(generator);
}


/**
* Generate CNT random points of DIM dimensions. Single thread.
*/
vector<t_point> my_gen(unsigned int dim, unsigned int cnt)
{
	vector<t_point> results;

	t_point p;
	p.resize(dim);

	for (unsigned int i = 0; i < cnt; i++)
	{
		for (unsigned int j = 0; j < dim; j++)
			p[j] = drand();
		results.push_back(p);
	}
	return results;
}

/**
* Helper function that just prints stuff.
*/
void print(vector<t_point> & data)
{
	for (t_point item : data)
	{
		for (double d : item)
			cout << d << "\t";
		cout << endl;
	}
}

/**
* Experimental function that:
*  Generates CNT random DIM-dimensional points
*
* This all will be done in THREADS threads.
* Note 1: launch::async is not required for MSVC, but must be used for gcc, otherwise it will allways run in single thread.
*
*/
void experiment(const unsigned int DIM, const unsigned int CNT, const unsigned int THREADS)
{
	chrono::time_point<chrono::system_clock> start, end;
	start = chrono::system_clock::now();

	vector<future<vector<t_point>>> my_futures;

	for (unsigned int i = 0; i < THREADS; i++)
	{
		my_futures.push_back(async(launch::async, my_gen, DIM, CNT / THREADS));
	}

	vector<t_point> my_points;
	vector<t_point> current;

	for (unsigned int i = 0; i < my_futures.size(); i++)
	{
		current = my_futures[i].get();
		my_points.insert(my_points.end(), current.begin(), current.end());
	}

	end = chrono::system_clock::now();
	chrono::duration<double> elapsed_seconds = end - start;

	if (CNT < 40)
		print(my_points);

	cout << "Generating data...\nData " << CNT << ", Threads " << THREADS << ", Elapsed time " << elapsed_seconds.count() << "s\n";
}

int main()
{	
	experiment(100, 1000000, 1);
	cout << endl;
	cout << endl;
	experiment(100, 1000000, 4);

	return 0;
}