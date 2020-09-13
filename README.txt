Project Blockchain:

This project simulates a Blockchain platform for secure transactions of virtual coins.
Simulation involves Miners mining blocks, while users perform VC transactions, and a dynamic system connecting them together.
Users/miners are simulated by concurrent threads.
The sample file will simulate the creation of 5 blocks and 4 transactions. It runs for a couple of minutes.


A correct format for a file input:

First line: three numbers sperated by spaces.
Second line: All the names of the users who send/receive money.
Next lines: two names and a number (first name is the sender, second is the receiver, number is amount of VS in transaction).


A brief summary for Blockchain for virtual currency concepts:

- The Blockchain is an abstract one directional chain of blocks.
- Each block contains data about virtual coin (VC) transactions.
- Blocks are created by miners, which are users who use computational power to create (mine) new blocks. They get a VS reward for each block created.
- Each block created is used as a platform that contains transaction records.
- The records of each block are of transactions made in the system since the previous block was created.
- These transactions are recorded on the chain and are secure, hence represent the real balance of the users.


Security enforcement and the mining concept:

- Each block contains:
1. Timestamp of creation moment
2. Non-reversible function's Hash value (Hex string) of the previous block (intial value: 0)
3. Hash value of the block itself
4. A magic number - the magic number is a randomly chosen number,
   such that the hash value of the block itself (containing the magic number) starts with N (to be defined) zeros.
5. The transactions, signed with a signature which can't be copied.

- All these together make it practically impossible to edit data (e.g, create fake transactions) while keeping the chain valid.
- The process of guessing magic numbers is called the mining process.
- As N grows, the complexity of guessing a magic number increases and hence takes longer to mine.
- N is dynamically tuned such that a block will take a specific range of mining time. 
- In this project N is tuned to take around a minute per block (for small N it is less than a second)




