package blockchain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<String> paramsAndTransactions = parseFile();

        String[] params = paramsAndTransactions.get(0).split(" ");

        // initial time-complexity level for mining a block.
        final int numZeros = Integer.parseInt(params[0]);

        // numMiners defines the number of simulated miners mining blocks.
        final int numMiners = Integer.parseInt(params[1]);

        // numBlocks represents the number of blocks to be mined until simulation is stopped.
        final int numBlocks = Integer.parseInt(params[2]);

        Blockchain blockChain = new Blockchain(numZeros);

        // represents users who are going to be sending/receiving VCs (Virtual Coins).
        List<String> names = Arrays.stream(paramsAndTransactions.get(1).split(" ")).collect(Collectors.toList());
        blockChain.users = names.stream().map(User::new).collect(Collectors.toList());

        // start executing transactions on a new thread
        List<String> transactions = paramsAndTransactions.subList(2,paramsAndTransactions.size());
        ExecutorService transactionsExecutor = Executors.newSingleThreadExecutor();
        transactionsExecutor.submit(() -> blockChain.sendTransactions(transactions));

        // while transactions are sent from users to users, blocks are mined by numMiners miners, concurrently.
        ExecutorService executor = Executors.newFixedThreadPool(numMiners);
        for (int i = 0; i < numBlocks; i++) {
            blockChain.mining = true;
            CompletionService<blockchain.Block> completion = new ExecutorCompletionService<>(executor);
            for (int j = 0; j < numMiners; j++) {
                completion.submit(blockChain::calculateBlock);
            }
            try {
                // waiting for first miner to successfully mine a block
                Block block = completion.take().get();

                // as first miner mines a block, print the block and the new users balance
                System.out.println(block.toString());
                System.out.println(blockChain.getUsersBalance());

                // preparing for next block mining
                blockChain.transactions.clear();
                blockChain.mining = false;
                blockChain.updateNumZeros(block.getGenerationTime());
                blockChain.chain.add(block);
                blockChain.size++;

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        transactionsExecutor.shutdown();
        executor.shutdown();
        blockChain.validate();
    }

    // Parses and validates the input file
    static List<String> parseFile() {
        boolean fileNotFound = false;
        boolean missingArgs = false;
        boolean missingUsers = false;
        Scanner filePathScanner = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        do {
            System.out.println("Please enter a valid file path: ");
            String filePath = filePathScanner.next();

            File file = new File(filePath);
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    lines.add(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + filePath);
                fileNotFound = true;
                continue;
            }
            for (int i = 0; i < lines.size(); i++) {
                if (i == 1) {
                    continue;
                }
                if (lines.get(i).split(" ").length != 3) {
                    System.out.println("Wrong file format: number of words in line " + i + " is not 3");
                    missingArgs = true;
                    break;
                }
            }
            Set<String> users = Arrays.stream(lines.get(1).split(" ")).collect(Collectors.toSet());
            for (int i = 2; i < lines.size(); i++) {
                String[] transaction = lines.get(i).split(" ");
                if (!users.contains(transaction[0]) || !users.contains(transaction[1])) {
                    System.out.println("Wrong file format: missing users in second line of file");
                    missingUsers = true;
                    break;
                }
            }
        } while (fileNotFound || missingArgs || missingUsers);

        return lines;
    }

}
