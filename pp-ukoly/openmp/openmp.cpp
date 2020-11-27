#include <iostream>
#include <iomanip>
#include <vector>
#include <memory>

class Matrix {
    std::pair<int, int> dimensions;

    public:
    Matrix(int x, int y) {
        dimensions = std::make_pair(x, y);
    }

    std::pair<int, int> getDimensions() {
        return dimensions;
    };
    virtual void setValue(int x, int y, int value) = 0;
    virtual int getValue(int x, int y) = 0;

    void printMatrix() {
        for (int i = 0; i < dimensions.second; i++) {
            std::cout << std::left;
            for (int j = 0; j < dimensions.first; j++) {
                std::cout << std::setw(6) << getValue(j, i);
            }
            std::cout << std::endl;
        }
    }

    bool static canMultiply(Matrix & first, Matrix & second) {
        return first.getDimensions().first == second.getDimensions().second; 
    }

    int static multiplyPosition(Matrix & first, Matrix & second, int x, int y) {
        const int dimension = first.getDimensions().second;
        int sum = 0;

        for (int i = 0; i < dimension; i++) {
            sum += first.getValue(x, i) * second.getValue(i, y);
        }

        return sum;
    }

    std::shared_ptr<std::vector<int>> static multiplyMatricesData(Matrix & first, Matrix & second) {
        std::shared_ptr<std::vector<int>> data = std::make_shared<std::vector<int>>();

        for (int i = 0; i < first.getDimensions().first; i++) {
            for (int j = 0; j < second.getDimensions().second; j++) {
                data -> push_back(multiplyPosition(first, second, i, j));
            }
        }

        return data;
    }
};

class Matrix2D : public Matrix {
    std::vector<std::vector<int>> data;

    public:
    Matrix2D(int x, int y, std::vector<int> & data) : Matrix(x, y) {        
        for (int i = 0; i < y; i++) {
            std::vector<int> row;
            for (int j = 0; j < x; j++) {
                row.push_back(data[i*x + j]);
            }
            this->data.push_back(row);
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
            first.getDimensions().first,
            second.getDimensions().second,
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
        data[x + y * getDimensions().first] = value;
    }

    int getValue(int x, int y) {
        return data[x + y * getDimensions().first];
    }

    std::shared_ptr<Matrix1D> static multiplyMatrices(Matrix & first, Matrix & second) {
        return std::make_shared<Matrix1D>(
            first.getDimensions().first,
            second.getDimensions().second,
            *Matrix::multiplyMatricesData(first, second)
        );
    }
};

int main() {
    std::vector<int> data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    std::shared_ptr<Matrix> matrix1 = std::make_shared<Matrix2D>(3, 4, data);
    std::shared_ptr<Matrix> matrix2 = std::make_shared<Matrix1D>(4, 3, data);

    matrix1 -> printMatrix();
    std::cout << std::endl;
    matrix2 -> printMatrix();
    std::cout << std::endl;

    std::shared_ptr<Matrix> matrix3 = Matrix2D::multiplyMatrices(*matrix1, *matrix2);
    matrix3 -> printMatrix();
    std::cout << std::endl;

    std::shared_ptr<Matrix> matrix4 = Matrix1D::multiplyMatrices(*matrix1, *matrix2);
    matrix4 -> printMatrix();
    std::cout << std::endl;

    std::shared_ptr<Matrix> matrix5 = Matrix2D::multiplyMatrices(*matrix2, *matrix1);
    matrix5 -> printMatrix();
    std::cout << std::endl;

    std::shared_ptr<Matrix> matrix6 = Matrix1D::multiplyMatrices(*matrix2, *matrix1);
    matrix6 -> printMatrix();
    std::cout << std::endl;

    return 1;
}