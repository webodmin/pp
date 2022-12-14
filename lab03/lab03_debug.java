import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Main {
    private int numberOfThreads = 1;
    private long combinedSum = 0;
    private int threadCounter = 0;

    static int arraySize = 50000000;
    public long[] array = new long[arraySize];
    int finishedOnCurrentPassThreadCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        long startMoment, endMoment;

        Semaphore semaphore = new Semaphore(2);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // Просим разрешение и ждём, пока не получим его
            e.printStackTrace();
        }
        System.out.println("Hello, World!");
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // Просим разрешение и ждём, пока не получим его
            e.printStackTrace();
        }
        System.out.println("Hello, World!");

        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            }
        };
        Thread thread = new Thread(task);
        System.out.println("Sleeping started");
        thread.start();
        //thread.interrupt(); // No sleeping really occurs
        thread.join();
        System.out.println("Sleeping finished");

        Object lock = new Object();

        Runnable task2 = () -> {
            synchronized (lock) {
                System.out.print("thread");
            }
        };

        Thread th1 = new Thread(task2);
        th1.start();
        synchronized (lock) {
            for (int i = 0; i < 8; i++) {
                Thread.sleep(1000);
                System.out.print("  " + i + "th1 state: " + th1.getState());
            }
            System.out.println(" ...");
        }

        for (int t = 2; t <= 64; t += 2) {
            startMoment = System.nanoTime();
            m.starter1(t);
            endMoment = System.nanoTime();
            System.out.printf("Час виконання програми для розміру масиву %d та числа тредів %d (мс): %d\n", arraySize, t, (endMoment - startMoment) / 1000 / 1000);
        }

        /*
        for (int s = 10; s <= 100000; s *= 10) {
            startMoment = System.nanoTime();
            m.starter2(s);
            //Thread.sleep(1000); // Sleeping for one second to check timers
            endMoment = System.nanoTime();
            System.out.printf("Час виконання програми для розміру масиву %d (мс): %d\n", s, (endMoment - startMoment) / 1000 / 1000);
        }

        */
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

    public void starter1(int t){
        numberOfThreads = t;
        int[] array = new int[arraySize];
        int[] startIndexes = new int[numberOfThreads];
        int[] endIndexes = new int[numberOfThreads];
        long simpleSum = 0;

        for (int i = 0; i < arraySize; i++)
            array[i] = i;

        PartialSummarizer ps = new PartialSummarizer(this, array, 0, arraySize-1);
        ps.run();
        for (int i = 0; i < arraySize; i++)
            simpleSum = simpleSum + array[i];
        //System.out.println("Сума в однопоточному режимі: " + simpleSum);
        //System.out.println("Сума в однопоточному режимі: " + ps.partialSum);

        for (int i = 0; i < numberOfThreads; i++) {
            startIndexes[i] = arraySize / numberOfThreads * i;
            //System.out.printf("startIndexes[%d]: %d\n", i, startIndexes[i]);
        }

        for (int i = 0; i < numberOfThreads - 1; i++) {
            endIndexes[i] = startIndexes[i + 1] - 1;
            //System.out.printf("endIndexes[%d]: %d\n", i, endIndexes[i]);
        }
        endIndexes[numberOfThreads - 1] = arraySize - 1;

        combinedSum = 0;
        threadCounter = 0;
        PartialSummarizer[] threadsOther = new PartialSummarizer[numberOfThreads];
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threadsOther[i] = new PartialSummarizer(this, array, startIndexes[i], endIndexes[i]);
            threads[i] = new Thread(threadsOther[i]);
            threads[i].start();
        }

        synchronized(this) {
            while (threadCounter < numberOfThreads) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //System.out.println("Сума в багатопоточному режимі: " + combinedSum);
    }

    public void starter2(int s) {

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
            System.out.printf("%s\n", Arrays.toString(array));

            passCounter++;
        } while (currentSize > 1);

        //System.out.println("\nСума в багатопоточному режимі: " + array[0]);
    }

    synchronized public void setPartialSum(long partSum){
        combinedSum = combinedSum + partSum;
        threadCounter++;
        notify();
    }

    synchronized public void increaseFinishedThreadCounter() {
        finishedOnCurrentPassThreadCounter++;
        notify();
    }

}
