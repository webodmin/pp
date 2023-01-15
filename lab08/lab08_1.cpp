#include <iostream>
#include <mpi.h>
#include <vector>

using namespace std;

int main(int argc, char* argv[]) {
    int size, rank;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    const int arraySize = 10000;
    const int chunkSize = arraySize / size;
    int array[arraySize];

    if (rank == 0) {
        for (int i = 0; i < arraySize; i++) {
            array[i] = i;
        }
    }

    vector<int> chunks(chunkSize);

    cout << "Hello from process #" << rank << endl;
    MPI_Scatter(array, chunkSize, MPI_INT, &chunks[0], chunkSize, MPI_INT, 0, MPI_COMM_WORLD);

    long sum = 0;
    for (int i = 0; i < chunkSize; i++) {
        sum += chunks[i];
    }

    long globalSum;
    MPI_Reduce(&sum, &globalSum, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        cout << "Sum of array elements is: " << globalSum << endl;
    }

    MPI_Finalize();
    return 0;
}
