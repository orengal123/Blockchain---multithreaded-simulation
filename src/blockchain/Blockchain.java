package blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import java.util.concurrent.ThreadLocalRandom;
import java.time.Duration;
import java.time.Instant;

class Blockchain {
    List<Transaction> transactions;
    List<User> users;
    List<blockchain.Block> chain;
    volatile int numZeros;
    volatile boolean mining;
    int size;
    int msgId;

    public Blockchain(int numZeros) {
        this.transactions = new ArrayList<>();
        this.users = new ArrayList<>();
        this.chain = new ArrayList<>();
        this.numZeros = numZeros;
        this.mining = true;
        this.size = 0;
        this.msgId = 1;
    }

    // returns a new block after a lengthy process of mining
    public Block calculateBlock() {
        long timeStamp;
        int magicNumber;
        String hash;
        String prevHash = size != 0 ? chain.get(size - 1).getHash() : "0";
        int id = size + 1;

        // mining and measure mining time
        Instant start = Instant.now();
        do {
            if (!mining) return null;
            timeStamp = new Date().getTime();
            magicNumber = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            hash = blockchain.StringUtil.applySha256(prevHash + id + timeStamp + magicNumber);
        } while (!hash.startsWith("0".repeat(numZeros)));
        Instant end = Instant.now();

        long generationTime = Duration.between(start, end).toSeconds();

        // dynamically increase/decrease time complexity to a stable period of a few seconds per block
        String numZerosOrientation = getNumZerosOrientation(generationTime);

        long minerId = Thread.currentThread().getId();
        return new blockchain.Block(prevHash, hash, id, timeStamp,
                magicNumber, generationTime, numZerosOrientation, minerId, new ArrayList<>(transactions));
    }

    void sendTransactions(List<String> transactionList) {
        for (String transaction : transactionList) {
            String[] transactionInfo = transaction.split(" ");
            String from = transactionInfo[0];
            String to = transactionInfo[1];
            int amount = Integer.parseInt(transactionInfo[2]);

            User sender = users.stream().filter(user -> from.equals(user.getName())).findAny().get();
            User receiver = users.stream().filter(user -> to.equals(user.getName())).findAny().get();

            String text = from + " sent " + to + " " + amount + " VC";

            if (sender.getBalance() < amount) {
                System.out.println("Transaction: '" + text + "' failed: Not enough virtual coins.");
                return;
            }
            try {
                // creating public key, private key and a signature for encryption of transaction
                KeyPairGenerator keyPairGen = Transaction.getKeyPairGenerator(1024);
                KeyPair keyPair = keyPairGen.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();
                byte[] signature = Transaction.sign(text, privateKey);
                Transaction finalTransaction = new Transaction(text, signature, msgId++, publicKey);
                transactions.add(finalTransaction);

            } catch (Exception e) {
                e.printStackTrace();
            }
            // update users' balance (actually unofficial balance,
            // official-secure balance may be calculated easily from the Blockchain)
            sender.decreaseBalance(amount);
            receiver.increaseBalance(amount);

            // simulated time between transactions
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void updateNumZeros(long generationTime)  {
        if (generationTime < 5) {
            numZeros++;
        } else if (generationTime > 20) {
            numZeros--;
        }
    }

    String getNumZerosOrientation(long generationTime) {
        if(generationTime < 10) return "N was increased to " + (numZeros + 1);
        else if(generationTime > 60) return "N was decreased by 1";
        else return "N stays the same";
    }

    String getUsersBalance() {
        String balance = "";
        for (User user : users) {
            balance += user.getName() + "'s balance: " + user.getBalance() + "VC\n";
        }
        return balance;
    }

    public boolean validate() {
        return validateAllBlockHash() && validateAllMessageId() && validateAllMessageSignature();
    }

    public boolean validateAllBlockHash() {
        if (!"0".equals(chain.get(0).getPrevHash())) {
            return false;
        }
        return IntStream.range(1, size).allMatch(i ->
                chain.get(i).getPrevHash().equals(chain.get(i - 1).getHash()));
    }

    public boolean validateAllMessageId() {
        long max = 0;
        for (Block block : chain) {
            if (block.getMaxId() <= max) {
                return false;
            }
            max = block.getMaxId();
        }
        return true;
    }

    public boolean validateAllMessageSignature() {
        return chain.stream().flatMap(block -> block.getTransactions().stream())
                .anyMatch(transaction -> !transaction.hasValidSignature());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block block : chain) {
            String toString = block.toString();
            sb.append(toString);
            sb.append("\nBalance:");
            for (User user : users) {
                sb.append("\n" + user.getName() + ": " + user.getBalance() + "VC");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
