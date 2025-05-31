## Student Project for Interactive Systems AI (SoSe 2025)

### Overview

This project is an AI-based application developed for the Interactive Systems AI course. It features an Alpha Beta Search for decision-making and an evolutionary learning algorithm for improving AI performance. The implementation is designed to run on Windows and includes scripts for evaluation and demonstration.

### Alpha Beta Search AI

The AI implementation utilizes an Alpha Beta Search. 
It is primarily handled by the `GameTree`, `GameTreeNode` and `BoardAnalyzer` classes.

### Evolutionary Learning Algorithm

The learning algorithm is handled by the `Evaluator` class, which calls `EvalScript.bat`. Note that this only works under Windows <br>
The results of the last iteration are saved within `res/evaluation`. Note that the directory gets cleared on each rerun.


### Running the application

To demo run the application under Windows, you can use the [`DemoScript.bat`](./DemoScript.bat) file.

