# Upper Level Genetic Algorithm

Like the Lower Level, this folder contains the implementation and resources needed to execute the Upper Level algorithm. Inside the `AGI` folder we can find the Lower Level implementation itself, which is necessary to call for each preference vector found by the Upper Level. On the other hand, inside the `AGS` folder we find the implementation of the latter. 

The files folder corresponds to the package where the Java classes that allow the reading and writing of files are located. Finally, in data we find the preferences of the agents, each of them with the name `agent_n`, where the `n` represents the n-th political party. Also here we find the population used. For example, the file `SGA_indiv21.txt` represents the 21st individual in the population. As we can see, each of these contains a vector of preferences with `0` or `1`, which represents rejecting or approving the law in the position in which it is found within the vector.

> [!INFO]
> As explained in the article, it should be noted that it was finally decided to use the strategy of `Test 1` in the `Unlimited` scenario and not the one provided by the Upper Level Algorithm. Even so, this algorithm has been used precisely to verify the strength of the strategies carried out.
