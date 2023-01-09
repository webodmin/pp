#include <iostream>
#include <cstdlib>
#include <omp.h>
#include <math.h>
using namespace std;

int arraySize = 0;
int parts = 0;
long* arr;
int* startIndexes;
int* endIndexes;

void fillArray() {
#pragma omp parallel for
    for (int i = 0; i < arraySize; i++) {
      // To fully load all CPU cores
      // arr[i] = static_cast<int>( (i*2)/cos(i) - sin(i) + atan(i) );
      arr[i] = i;
    }
}

void displayArray() {
    for (int i = 0; i < arraySize; i++) {
        cout << arr[i] << " ";
    }
    cout << endl;
}

long long sumArray() {
    long long sum = 0;
    for (int i = 0; i < arraySize; i++) {
        sum += arr[i];
    }
    return sum;
}

long long sumArrayParts() {
    long long sum = 0;
    omp_set_num_threads(parts);
#pragma omp parallel for
    for (int t = 0; t < parts ; t++) {
        long long sumOnePart = 0;
				printf("Method #1: Thread%d\n", omp_get_thread_num());
				for (int i = startIndexes[t]; i <= endIndexes[t]; i++) {
						sumOnePart += arr[i];
				}
        sum += sumOnePart;
    }
    return sum;
}

long long sumArrayPairs(int currentSize) {
    long long sum = 0;
    int passCounter = 1;
    omp_set_num_threads(currentSize/2);
    do {
        //printf("Початок проходу номер %d при кількості елементів масиву %d\n", passCounter, currentSize);
#pragma omp parallel for
        for (int leftIndex = 0; leftIndex < currentSize / 2; leftIndex++) {
            printf("Method #2: Thread%d\n", omp_get_thread_num());
            arr[leftIndex] = arr[leftIndex] + arr[currentSize-leftIndex-1];
        }
        currentSize = currentSize / 2 + currentSize % 2;
        // for debug
        /* long sumIntermediate = 0;
        for (int i = 0; i < currentSize; i++) {
            //printf("%d ", array[i]);
            sumIntermediate = sumIntermediate + arr[i];
        } */
        passCounter++;
    } while (currentSize > 1);
    return arr[0];
}


int main() {
    cout << "Enter the arraySize of the array: ";
    cin >> arraySize;
    cout << "Enter the number of parts for method #1: ";
    cin >> parts;

    arr = new long[arraySize];
    startIndexes = new int[parts];
    endIndexes = new int[parts];

    fillArray();
    cout << "Array: ";
    //displayArray();
    for (int i = 0; i < parts; i++) {
        startIndexes[i] = arraySize / parts * i;
        printf("startIndexes[%d]: %d\n", i, startIndexes[i]);
    }

    for (int i = 0; i < parts - 1; i++) {
        endIndexes[i] = startIndexes[i + 1] - 1;
        printf("endIndexes[%d]: %d\n", i, endIndexes[i]);
    }
    endIndexes[parts - 1] = arraySize - 1;
    omp_set_num_threads(4);
    long long sumParts = 0;
    long long sumPairs = 0;
//#pragma omp parallel sections
    {
//#pragma omp section
        {
            //printf("Thread%d\n", omp_get_thread_num());
            sumParts = sumArrayParts();
        }

/*
        {
            //sumPairs = sumArrayPairs(arraySize);
        }
*/
    }
    sumPairs = sumArrayPairs(arraySize);
    cout << "Sum of array elements by parts (method 1): " << sumParts << endl;
    cout << "Sum of array elements in pairs (method 2): " << sumPairs << endl;
    delete[] arr;
    delete[] startIndexes;
    delete[] endIndexes;
    return 0;
}


