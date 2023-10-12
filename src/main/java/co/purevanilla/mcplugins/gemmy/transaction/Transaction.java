package co.purevanilla.mcplugins.gemmy.transaction;

public class Transaction {

    public final boolean withdraw;
    public final int voting;
    public final int gems;
    public final int shops;
    public final int pay;
    public final int others;
    public final int balance;

    Transaction(boolean withdraw, int voting, int gems, int shops, int pay, int other, int balance){
        this.withdraw=withdraw;
        this.voting=voting;
        this.gems=gems;
        this.shops=shops;
        this.pay=pay;
        this.others=other;
        this.balance=balance;
    }

}
