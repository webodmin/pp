class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.starter();
    }

    public void starter() {
        int size = 1000000;
        long SumMas = 0;

        long[] mas = new long[size];

        for (int i = 0; i < size; i++)
            mas[i] = i;

        for (int i = 0; i < size; i++)
            SumMas = SumMas + mas[i];

        System.out.println("Сума в однопоточному режимі:");
        System.out.println(SumMas);

        System.out.println("Сума в багатопоточному режимі:");
        System.out.println(findArraySum(mas));
    }

    public long findArraySum(long[] mas) {
        int size = mas.length;

        do {
            for (int i = 0; i < size / 2; i++) {
                int end = size - 1 - i;
                mas[i] = mas[i] + mas[end];
            }

            size = size / 2 + size % 2;
        } while (size > 1);

        return mas[0];
    }
}
