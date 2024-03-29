﻿**PROJECT TITLE: Auto
**GROUP: G15

NAME1: Diogo Cruz, NR1: up201105483, GRADE1: 17, CONTRIBUTION1: 23.3%

NAME2: Luís Barbosa, NR2: up201405729, GRADE2: 18, CONTRIBUTION2: 30%

NAME3: Paulo Santos, NR3: up201403745, GRADE3: 17, CONTRIBUTION3: 23.3%

NAME4: Sérgio Ferreira, NR4: up201403074, GRADE4: 17, CONTRIBUTION4: 23.3%
 
** SUMMARY: 

An autotuner for C programs. The user adds pragmas to the source code and the auto-tuner automatically determines the best values for some constants.
Pragmas must be within functions, the programmer has to declare the variable of the first pragma and initialize the variable of the second pragma before the scope of these.

The available pragmas are:
1. #pragma tuner explore STEP(x,y) reference(STEP=z) (x > y, y > z > x, step is 1)
   #pragma tuner max_abs_error acc e
2. #pragma tuner explore STEP(x,y,z) reference(STEP=k) (x > y, y > k > x, z is the step)
   #pragma tuner max_abs_error acc e
3. #pragma tuner random RANDOM(x,y,N) reference(RANDOM=z) (x > y, y > z > x, N is the number of random numbers to generate)
   #pragma tuner max_abs_error acc e


** EXECUTE: 

Initially, the files that are generated by JJTree and JavaCC are already in the JJTree folder. 
However, if you prefer to generate new files you can run the script 'JJTreeRunner', considering your operating system.
After that, go to the scripts folder and run the script 'CompileAuto', followed by 'ExecuteAuto', considering your operating system.
 
**DEALING WITH SYNTACTIC ERRORS:

The syntactic analyser is implemented in JJTree. If any error occur, our tool goes to the next test file.
 
**SEMANTIC ANALYSIS:

In our tool, we consider important to implement five semantic rules. They are:
- Verify if the pragma data types are equal: we look for all the values used in the pragma, and make sure that they are all integers or doubles.
- Verify if the interval indicated in the pragma is in the correct order.
- Verify if is possible to pass through the value referenced in the pragma, considering the interval that is declared, and the step.
- Verify if for each starting pragma exists a compatible ending pragma.
- Verify if a pragma only refers to one variable.

Everytime this verifications don't apply, an exception is raised with an appropriate message.
 
**INTERMEDIATE REPRESENTATIONS (IRs): 

Considering the pragmas from the summary section, these are their HIRs:

1.

tuner
 explore
  STEP
   x
   y
  reference
   STEP
   z
 max_abs_error
  acc
  e

2.

tuner
 explore
  STEP
   x
   y
   z
  reference
   STEP
   k
 max_abs_error
  acc
  e

3. 

tuner
 random
  RANDOM
   x
   y
   N
  reference
   RANDOM
   z
 max_abs_error
  acc
  e
 
**CODE GENERATION: 

At this stage, we check in all pragmas if some of them have the same name. If not, we proceed to change the C code.
We have templates and change them with the correct information according to the file that is being used.
After that, a file with the new code is created and executed.
 
**OVERVIEW:

We have developed our program in Java using JavaCC as a third-party tool.
We have used JJTree to generate a syntax tree of a pragma, verifying if it is lexical and syntactically correct.
For each pragma found, our program sends the pragma line to JJTree in order to verify the pragma and generate the tree. Our program merges the trees of two corresponding pragmas.
This trees are CSTs and become ASTs on the semantic analysis. The ASTs are then used to verify if the pragma instruction is semantically correct and to adjust the template C code
that is added to the input C file to perform the measurements.

**TESTSUITE AND TEST INFRASTRUCTURE: 

In order to test our tool, we have six test files in the folder 'c_files' that are always run when we execute our tool.
With that, we can always test if our modifications are correct, in order to improve our compiler.
 
**TASK DISTRIBUTION:

Diogo: Implemented the Log, the result analysis, the analysis of max_abs_error in CodeChanger, the CodeExecutor & Pragma files, helped with scripts & Command.
Luís: Implemented the classes Auto (except the log), Command, Node, Parser and PragmaScope. Implemented a big part of CodeChanger and half of SemanticAnalyser.
Paulo: Implemented pragma STEP(x,y,z), developed functions in SemanticAnalyser and in CodeChanger, helped developing the pragma RANDOM and the JJTree.
Sérgio: Implemented pragma RANDOM(x,y,z) and some functions in semantic analysis, including changes on templates and helped developing the JJTree.
 
**PROS: 

We think that our tool is very well structured and is easy to implement more pragmas if we want to add more.
It can be executed in Windows or Linux.
Is very complete in terms of verifications, always giving messages to the user when something goes wrong.

 
**CONS: 

A possible improvement would be to send the time printfs to a file in code C.
