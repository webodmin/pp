class Main {
    private final int numberOfThreads = 3;
    private long combinedSum = 0;
    private int threadCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        Main m = new Main();
        m.starter();
    }

    public void starter(){
        int arraySize = 200;
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
        System.out.println("Сума в однопоточному режимі: " + simpleSum);
        System.out.println("Сума в однопоточному режимі: " + ps.partialSum);

        for (int i = 0; i < numberOfThreads; i++) {
            startIndexes[i] = arraySize / numberOfThreads * i;
            System.out.printf("startIndexes[%d]: %d\n", i, startIndexes[i]);
        }

        for (int i = 0; i < numberOfThreads - 1; i++) {
            endIndexes[i] = startIndexes[i + 1] - 1;
            System.out.printf("endIndexes[%d]: %d\n", i, endIndexes[i]);
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

        System.out.println("Сума в багатопоточному режимі: " + combinedSum);
    }

    synchronized public void setPartialSum(long partSum){
        combinedSum = combinedSum + partSum;
        threadCounter++;
        notify();
    }
}
