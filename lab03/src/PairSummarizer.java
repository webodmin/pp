import java.util.concurrent.BrokenBarrierException;

class PairSummarizer implements Runnable {

    Main main;
    int currSize;
    int index;

    // Constructor
    PairSummarizer(Main main, int leftIndex, int size) {
        this.main = main;
        index = leftIndex;
        currSize = size;
    }

    @Override
    public void run() {
        // System.out.printf("Thread %s started\n", Thread.currentThread().getName());
        main.array[index] = main.array[index] + main.array[currSize-index-1];
        try {
            main.cb.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
