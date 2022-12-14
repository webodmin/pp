import java.util.concurrent.BrokenBarrierException;

class PartialSummarizer implements Runnable {

    public long partialSum = 0;
    long[] array;
    int startIndex, endIndex;
    Main main;

    // Constructor
    PartialSummarizer(Main main, long[] curr_mas, int curr_begin, int curr_end) {
        array = curr_mas;
        startIndex = curr_begin;
        endIndex = curr_end;
        this.main = main;
    }

    @Override
    public void run() {
        for (int i = startIndex; i <= endIndex; i++) {
            partialSum = partialSum + array[i];
        }
        main.setPartialSum(partialSum);
        try {
            main.cb.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
