with Ada.Text_IO;
use Ada.Text_IO;

-- To monitor threads count in linux run this command in shell: while true ; do ps -eLf | wc -l ; sleep 0.2 ; done
-- On my notebook with default settings the maximum number is about 34631

procedure lab04 is
  arraySize: constant Integer := 32000;
  maxTasksLab01: Integer := 16; -- number of threads for method 1
  type IntArray is array (1..arraySize) of Long_Long_Integer;
  arr: IntArray;

  function part_sum(left: Integer; right: Integer) return Long_Long_Integer is
    sum: Long_Long_Integer := 0;
    i: Integer;
  begin
    i := left;
    while i <= right loop
      sum := sum + Long_Long_Integer(arr(i));
      i := i + 1;
    end loop;
    return sum;
  end part_sum;

  -- For checking
  function sum_of_arithmetic_progression(n: Integer) return Long_Long_Integer is
    a1: Integer := 0;
    d: Integer := 1;
  begin
    return Long_Long_Integer((2 * a1 + d * (n - 1)) * n / 2);
  end sum_of_arithmetic_progression;

  procedure create_array is
  begin
    for i in arr'Range loop
      arr(i) := Long_Long_Integer(i)-1;
    end loop;
    New_Line;
  end create_array;

  procedure show_array is
  begin
    for i in arr'Range loop
      Put(arr(i)'img & " ");
    end loop;
    New_Line;
  end show_array;

  task type MyTask1 is
    entry start(left, right : in Integer);
    entry finish(sum1 : out Long_Long_Integer);
  end MyTask1;

  task body MyTask1 is
    left, right : Integer;
  begin
    accept start(left, right : in Integer) do
      MyTask1.left := left;
      MyTask1.right := right;
    end start;
    accept finish (sum1 : out Long_Long_Integer) do
      sum1 := part_sum(left, right);
    end finish;
  end MyTask1;

  tasksLab01 : array(1..maxTasksLab01) of MyTask1;

  task type MyTask2 is
    entry start(left, size : in Integer);
  end MyTask2;

  task body MyTask2 is
    left, curr_size : Integer;
  begin
    accept start(left, size : in Integer) do
      MyTask2.left := left;
      MyTask2.curr_size := size;
    end start;
    arr(left) := arr(left) + arr(curr_size-left+1);
  end MyTask2;

  tasksLab02 : array(1..arraySize-1) of MyTask2;

  grandTotal: Long_Long_Integer := 0;
  partialResult: Long_Long_Integer;
  taskCounter: Integer := 0;
  currentSize: Integer := arraySize;
  startIndexes, endIndexes: array(1..maxTasksLab01) of Integer;
  passCounter: Integer := 1;
begin
  create_array;
  --show_array;
  for t in 1..maxTasksLab01 loop
    startIndexes(t) := arraySize / maxTasksLab01 * (t - 1) + 1;
  end loop;
  for t in 1..maxTasksLab01-1 loop
    endIndexes(t) := startIndexes(t + 1) - 1;
  end loop;
  endIndexes(maxTasksLab01) := arraySize;

  for t in tasksLab01'Range loop
    --Put_Line("Passing data to task number " & t'img & " of total " & maxTasksLab01'img & "...");
    tasksLab01(t).start(startIndexes(t),endIndexes(t));
    tasksLab01(t).finish(partialResult);
    grandTotal := grandTotal + Long_Long_Integer(partialResult);
  end loop;
  Put_Line("FINAL RESULT after all tasks completed (lab01 method): " & grandTotal'img);
  Put_Line("CHECKING RESULT with sum of arithmetic progression formula: " & sum_of_arithmetic_progression(arr'Length)'Image);

  while currentSize > 1 loop
    Put_Line("taskCounter at the beginning of a pass N" & passCounter'img & " = " & taskCounter'img & ". currentSize = " & currentSize'img);
    for index in 1..currentSize/2 loop
      taskCounter := taskCounter + 1;
      taskslab02(taskCounter).start(index,currentSize);
    end loop;
    currentSize := currentSize / 2 + currentSize mod 2;
    passCounter := passCounter + 1;
  end loop;
  Put_Line("FINAL RESULT after all tasks completed (lab02 method): " & arr(1)'img);
end;
