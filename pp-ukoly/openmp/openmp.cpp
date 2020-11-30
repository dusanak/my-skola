#include <iostream>
#include <iomanip>
#include <vector>
#include <memory>
#include <chrono>
#include <omp.h>

class Matrix {
    int x;
    int y;

    public:
    Matrix(int x, int y) {
        this -> x = x;
        this -> y = y;        
    }

    int getX() {
        return x;
    };

    int getY() {
        return y;
    };

    virtual void setValue(int x, int y, int value) = 0;
    virtual int getValue(int x, int y) = 0;

    void printMatrix() {
        for (int i = 0; i < getY(); i++) {
            std::cout << std::left;
            for (int j = 0; j < getX(); j++) {
                std::cout << std::setw(6) << getValue(j, i);
            }
            std::cout << std::endl;
        }
    }

    bool static canMultiply(Matrix & first, Matrix & second) {
        return first.getX() == second.getY(); 
    }

    int static multiplyPosition(Matrix & first, Matrix & second, int x, int y) {
        const int dimension = first.getY();
        int sum = 0;

        for (int i = 0; i < dimension; i++) {
            sum += first.getValue(x, i) * second.getValue(i, y);
        }

        return sum;
    }

    std::shared_ptr<std::vector<int>> static multiplyMatricesData(Matrix & first, Matrix & second) {
        std::shared_ptr<std::vector<int>> data = std::make_shared<std::vector<int>>(first.getX() * second.getY());

        #pragma omp parallel for
        for (int i = 0; i < first.getX(); i++) {
            for (int j = 0; j < second.getY(); j++) {
                (*data)[i * second.getY() + j] = multiplyPosition(first, second, i, j);
            }
        }

        return data;
    }
};

class Matrix2D : public Matrix {
    std::vector<std::vector<int>> data;

    public:
    Matrix2D(int x, int y, std::vector<int> & data) : Matrix(x, y) {
        this->data = std::vector<std::vector<int>>(y);
        for (int i = 0; i < y; i++) {
            this->data[i] = std::vector<int>(x);
            for (int j = 0; j < x; j++) {
                this->data[i][j] = data[i*x + j];
            }
        }
    }

    void setValue(int x, int y, int value) {
        data[y][x] = value;
    }

    int getValue(int x, int y) {
        return data[y][x];
    }

    std::shared_ptr<Matrix2D> static multiplyMatrices(Matrix & first, Matrix & second) {
        return std::make_shared<Matrix2D>(
            first.getX(),
            second.getY(),
            *Matrix::multiplyMatricesData(first, second)
        );
    }
};

class Matrix1D : public Matrix {
    std::vector<int> data;

    public:
    Matrix1D(int x, int y, std::vector<int> & data) : Matrix(x, y) {        
        this->data = data;
    }

    void setValue(int x, int y, int value) {
        data[x + y * getX()] = value;
    }

    int getValue(int x, int y) {
        return data[x + y * getX()];
    }

    std::shared_ptr<Matrix1D> static multiplyMatrices(Matrix & first, Matrix & second) {
        return std::make_shared<Matrix1D>(
            first.getX(),
            second.getY(),
            *Matrix::multiplyMatricesData(first, second)
        );
    }
};

int main(int argc, char * argv[]) {
    omp_set_num_threads(1);
    bool use_1d_matrix = false;
    int matrix_size = 1024;

    if (argc == 4) {
        omp_set_num_threads(std::atoi(argv[1]));
        use_1d_matrix = std::atoi(argv[2]);
        matrix_size = std::atoi(argv[3]);
    }

    std::vector<int> data;
    for (int i = 0; i < matrix_size*matrix_size; i++) {
        data.push_back(i % 64);
    }

    auto start = std::chrono::high_resolution_clock::now();

    if (!use_1d_matrix) {
        std::shared_ptr<Matrix> matrix1 = std::make_shared<Matrix2D>(matrix_size, matrix_size, data);
        std::shared_ptr<Matrix> matrix2 = std::make_shared<Matrix2D>(matrix_size, matrix_size, data);
        std::shared_ptr<Matrix> matrix3 = Matrix2D::multiplyMatrices(*matrix1, *matrix2);
    } else {
        std::shared_ptr<Matrix> matrix1 = std::make_shared<Matrix1D>(matrix_size, matrix_size, data);
        std::shared_ptr<Matrix> matrix2 = std::make_shared<Matrix1D>(matrix_size, matrix_size, data);
        std::shared_ptr<Matrix> matrix3 = Matrix1D::multiplyMatrices(*matrix1, *matrix2);
    }

    auto end = std::chrono::high_resolution_clock::now();

    int64_t time = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count() / 1000;

    std::cout << time << std::endl;

    return time;
}