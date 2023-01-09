#include <iostream>
#include <cstdlib>
using namespace std;

int arraySize = 0;
int parts = 0;
long* arr;
int* startIndexes;
int* endIndexes;

void fillArray() {
    for (int i = 0; i < arraySize; i++) {
        //arr[i] = rand() % 100;
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
    for (int t = 0; t < parts ; t++) {
        long long sumOnePart = 0;
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
    do {
        //printf("Початок проходу номер %d при кількості елементів масиву %d\n", passCounter, currentSize);
        for (int leftIndex = 0; leftIndex < currentSize / 2; leftIndex++) {
            arr[leftIndex] = arr[leftIndex] + arr[currentSize-leftIndex-1];
        }
        currentSize = currentSize / 2 + currentSize % 2;
        long sumIntermediate = 0;
        for (int i = 0; i < currentSize; i++) {
            //printf("%d ", array[i]);
            sumIntermediate = sumIntermediate + arr[i];
        }
        passCounter++;
    } while (currentSize > 1);
    return arr[0];
}


int main() {
    cout << "Enter the arraySize of the array: ";
    cin >> arraySize;
    cout << "Enter the number of parts: ";
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

    int sumParts = 0;
    int sumPairs = 0;
    {
        {
            //sumParts = sumArray(arr, arraySize);
        }
        {
            //sumPairs = sumArrayPairs(arraySize);
        }
    }
    cout << "Sum of array elements by parts (method 1): " << sumArrayParts() << endl;
    cout << "Sum of array elements in pairs (method 2): " << sumArrayPairs(arraySize) << endl;
    delete[] arr;
    delete[] startIndexes;
    delete[] endIndexes;
    return 0;
}


