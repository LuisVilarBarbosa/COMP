#!/bin/bash
cd .. || exit
java -cp bin tuner/Auto testsuite/*.c
cd scripts
echo "Press any key to continue . . ."
read -r var
