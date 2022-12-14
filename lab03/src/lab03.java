import java.util.concurrent.CyclicBarrier;

class Main {
    private int numberOfThreads = 1;
    private long combinedSum = 0;

    static int arraySize = 500000000;
    public long[] array = new long[arraySize];
    public static CyclicBarrier cb = null;

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        long startMoment, endMoment;
        long simpleSum = 0;

        m.initArray();

        for (int i = 0; i < arraySize; i++)
            simpleSum = simpleSum + m.array[i];
        System.out.println("Сума простим підрахунком у потоці main: " + simpleSum);

        for (int t = 2; t <= 32; t += 2) {
            startMoment = System.nanoTime();
            m.starter1(t);
            endMoment = System.nanoTime();
            System.out.printf("Час виконання програми для розміру масиву %d та числа потоків %d (мс): %d\n", arraySize, t, (endMoment - startMoment) / 1000 / 1000);
        }
        System.out.println("==========================================================================================");
        for (int s = 10; s <= 10000; s *= 10) {
            startMoment = System.nanoTime();
            m.starter2(s);
            endMoment = System.nanoTime();
            System.out.printf("Час виконання програми для розміру масиву %d (мс): %d\n\n", s, (endMoment - startMoment) / 1000 / 1000);
        }
    }

    private void initArray() {
        for (int i = 0; i < arraySize; i++)
            array[i] = i;
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
            System.out.printf("Проста сума елементів масиву на %d елементів: %d\n", arraySize, sum);
    }

    public void starter1(int t){
        numberOfThreads = t;
        int[] startIndexes = new int[numberOfThreads];
        int[] endIndexes = new int[numberOfThreads];
        long simpleSum = 0;
        cb = new CyclicBarrier(numberOfThreads, new BarrierAction(this));

        for (int i = 0; i < numberOfThreads; i++) {
            startIndexes[i] = arraySize / numberOfThreads * i;
        }

        for (int i = 0; i < numberOfThreads - 1; i++) {
            endIndexes[i] = startIndexes[i + 1] - 1;
        }
        endIndexes[numberOfThreads - 1] = arraySize - 1;

        combinedSum = 0;
        PartialSummarizer[] threadsOther = new PartialSummarizer[numberOfThreads];
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threadsOther[i] = new PartialSummarizer(this, array, startIndexes[i], endIndexes[i]);
            threads[i] = new Thread(threadsOther[i]);
            threads[i].start();
        }

        // Waiting for worker threads to finish
        synchronized(this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.printf("Сума у багатопоточному режимі за методом №1 при кількості потоків %d: %d\n", numberOfThreads, combinedSum);
    }

    public void starter2(int s) {
        arraySize = s;

        int passCounter = 1;
        int currentSize = arraySize;

        initArray();

        PairSummarizer[] pthreads = new PairSummarizer[currentSize / 2];
        Thread[] threads = new Thread[currentSize / 2];

        do {
            cb = new CyclicBarrier(currentSize / 2, new BarrierAction(this));
            // System.out.printf("Початок проходу номер %d при кількості елементів масиву %d\n", passCounter, currentSize);
            for (int i = 0; i < currentSize / 2; i++) {
                pthreads[i] = new PairSummarizer(this, i, currentSize);
                threads[i] = new Thread(pthreads[i]);
                threads[i].start();
            }

            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

            currentSize = currentSize / 2 + currentSize % 2;
            passCounter++;

        } while (currentSize > 1);

        System.out.printf("Сума в багатопоточному режимі за методом №2 при кількості елементів %d: %d\n", arraySize, array[0]);
    }

    public void setPartialSum(long partSum){
        combinedSum = combinedSum + partSum;
    }

    synchronized public void goToNextPass() {
        notify();
    }

    // Дії, які виконуються при досягненні бар'єру
    public static class BarrierAction implements Runnable {
        Main m;

        BarrierAction(Main m) {
            this.m = m;
        }

        @Override
        public void run() {
            m.goToNextPass();
        }
    }
}
