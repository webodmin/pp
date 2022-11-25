import java.util.Arrays;

class Main {

    int arraySize = 1000000;
    public long[] array = new long[arraySize];
    int finishedOnCurrentPassThreadCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        for (int s = 10; s <= 10000000; s *= 10) {
            long startMoment = System.nanoTime();
            m.starter(s);
            long endMoment = System.nanoTime();
            System.out.printf("Час виконання програми для розміру масиву %d (мс): %d\n\n", s, (endMoment - startMoment) / 1000 / 1000);
        }
    }

    private void printArray(boolean showElements, boolean showSum) {
        long sum = 0;
        if (showElements) {
            for (int i = 0; i < arraySize; i++) {
                sum = sum + array[i];
                System.out.printf("%d ", array[i]);
            }
        } else {
            for (int i = 0; i < arraySize; i++) {
                sum = sum + array[i];
            }
        }
        if (showSum)
            System.out.printf("Сума елементів масиву: %d\n", sum);
    }

    public void starter(int s) {

        arraySize = s;

        long simpleSum = 0;
        int passCounter = 1;
        int currentSize = arraySize;

        // Array initialization
        for (int i = 0; i < arraySize; i++)
            array[i] = i;

        // Show sum when no threads are used
        printArray(false, true);

        PairSummarizer[] pthreads = new PairSummarizer[currentSize / 2];
        Thread[] threads = new Thread[currentSize / 2];

        do {
            //System.out.printf("Початок проходу номер %d при кількості елементів масиву %d\n", passCounter, currentSize);
            for (int i = 0; i < currentSize / 2; i++) {
                pthreads[i] = new PairSummarizer(this, i, currentSize);
                threads[i] = new Thread(pthreads[i]);
                threads[i].start();
            }

            synchronized (this) {
                while (finishedOnCurrentPassThreadCounter < currentSize / 2) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            currentSize = currentSize / 2 + currentSize % 2;
            finishedOnCurrentPassThreadCounter = 0;
            long sumIntermediate = 0;
            for (int i = 0; i < currentSize; i++) {
                //System.out.printf("%d ", array[i]);
                sumIntermediate = sumIntermediate + array[i];
            }
            //printArray(false,true);
            //System.out.printf("\nСума частини перших %d елементів масиву проміжна на проході номер %d: %d\n", currentSize, passCounter, sumIntermediate);
            //System.out.printf("%s\n", Arrays.toString(array));

            passCounter++;
        } while (currentSize > 1);

        System.out.println("Сума в багатопоточному режимі: " + array[0]);
    }

    synchronized public void increaseFinishedThreadCounter() {
        finishedOnCurrentPassThreadCounter++;
        notify();
    }

}
