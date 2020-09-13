package blockchain;

import java.util.List;

class Block {

    private String prevHash;
    private String hash;
    private int id;
    private long timeStamp;
    private int magicNumber;
    private long generationTime;
    private String numZerosOrientation;
    private long minerId;
    private volatile List<Transaction> transactions;

    public Block(String prevHash, String hash, int id, long timeStamp, int magicNumber,
                 long generationTime, String numZerosOrientation, long minerId, List<Transaction> transactions) {
        this.prevHash = prevHash;
        this.hash = hash;
        this.id = id;
        this.timeStamp = timeStamp;
        this.magicNumber = magicNumber;
        this.generationTime = generationTime;
        this.numZerosOrientation = numZerosOrientation;
        this.minerId = minerId;
        this.transactions = transactions;
    }

    public String getPrevHash() { return prevHash; }
    public String getHash() {
        return hash;
    }
    public long getGenerationTime() { return generationTime; }
    public List<Transaction> getTransactions() { return transactions; }

    public long getMaxId() {
        return transactions.stream().mapToLong(Transaction::getId).max().orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder transactionMessages = new StringBuilder();
        for (Transaction transaction : transactions) {
            transactionMessages.append("\n").append(transaction.getText());
        }
        return "======================== new Block ========================" +
                "\nCreated by miner #" + minerId +
                "\nminer #" + minerId + " receives 100 VC for mining the block" +
                "\nId: " + id +
                "\nTimestamp: " + timeStamp +
                "\nMagic number: " + magicNumber +
                "\nHash of the previous block:" +
                "\n" + prevHash +
                "\nHash of the block:" +
                "\n" + hash +
                "\nBlock data:" +
                transactionMessages +
                "\nBlock was generating for " + generationTime + " seconds" +
                "\n" + numZerosOrientation + "\n";
    }
}
