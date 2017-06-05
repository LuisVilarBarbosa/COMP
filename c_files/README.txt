CommentedExample.c:
Result: "No tests will be performed. Fix any warning or error given by the compiler."

Used to show that when a pragma is commented, nothing will be tested.

ComplexExample.c:
Result: Best execution of VAR: null, with value of null and execution time of null
Reference of VAR: 1.0 with a value of null
Best execution of STEP: 1.000000, with value of 1760.8 and execution time of 15711.2
Reference of STEP: 1.0 with a value of 1760.8
Best execution of VAR2: 7.000000, with value of 11.2 and execution time of 0.0
Reference of VAR2: 1.0 with a value of 1.6

It's a complex example used to show that is possible to have pragmas in different functions,
and in the same function is possible to have more than one pragma, creating a scope.

DoubleExample.c:
Result: Best execution of STEP: 8.400000, with value of 6.728 and execution time of 78.8
Reference of STEP: 2.4 with a value of 1.928

Example that shows that is possible to use doubles in the pragma, but they all need to have
the same type.

RandomExample.c:
Result: Best execution of RANDOM: 1.000000, with value of 2.8 and execution time of 0.0
Reference of RANDOM: 1.0 with a value of 2.8

Used to show the pragma random.

SimpleExample.c:
Result: Best execution of STEP: 6.000000, with value of 6.8 and execution time of 0.0
Reference of STEP: 1.0 with a value of 2.8

Used to show pragma step with an increment of 1.

STEP2Example.c:
Result: Best execution of STEP: 7.000000, with value of 7.6 and execution time of 0.0
Reference of STEP: 1.0 with a value of 2.8

Used to show pragma step with a different increment.