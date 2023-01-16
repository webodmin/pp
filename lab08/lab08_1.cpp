#include <iostream>
#include <mpi.h>

using namespace std;

int main(int argc, char** argv) {
    const int array_size = 1000;
    int size, rank;
    int data[array_size];
    int local_sum, global_sum;

    // Initialize the data array
    for (int i = 0; i < array_size; i++) {
        data[i] = i;
    }

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // Calculate the local sum of the portion of the array assigned to this process
    int start = rank * array_size / size;
    int end = (rank + 1) * array_size / size;
    local_sum = 0;
    for (int i = start; i < end; i++) {
        local_sum += data[i];
    }

    // For debug
    // cout << "Process with rank " << rank << ", start = " << start << ", end = " << end << ", local_sum = " << local_sum << endl;

    // Use MPI_Reduce to calculate the global sum
    MPI_Reduce(&local_sum, &global_sum, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        std::cout << "The sum of the elements in the array with size " << array_size << " is: " << global_sum << std::endl;
    }

    MPI_Finalize();
    return 0;
}
