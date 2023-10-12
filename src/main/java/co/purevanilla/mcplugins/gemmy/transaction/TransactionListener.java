package co.purevanilla.mcplugins.gemmy.transaction;

import co.purevanilla.mcplugins.gemmy.Main;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.maxgamer.quickshop.api.event.ShopPurchaseEvent;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class TransactionListener implements Listener {

    Map<UUID, List<Transaction>> transactions;
    Connection connection;
    Plugin plugin;

    public TransactionListener(Plugin plugin) throws SQLException, IOException, ClassNotFoundException {
        this.transactions=new HashMap<>();
        this.plugin=plugin;
        this.openConnection();
        DatabaseMetaData dbm = this.connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, getTableName(), null);
        if (!tables.next()) {
            this.plugin.getLogger().info("creating table");
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(
                    this.streamToString(this.plugin.getResource("creation.sql")).replaceAll("%prefix%", Objects.requireNonNull(this.plugin.getConfig().getString("mariadb.prefix")))
            );
            statement.close();
        } else {
            this.plugin.getLogger().info("table already exists");
        }
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, ()->{
            try {
                flush();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 0, 20*60*2);
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        ConfigurationSection config = this.plugin.getConfig().getConfigurationSection("mariadb");
        assert config != null;
        this.connection = DriverManager.getConnection(
                "jdbc:mariadb://"+config.getString("host")+
                        ":"+String.valueOf(config.getLong("port"))+"/"+
                        config.getString("database"),
                config.getString("user"),
                config.getString("password")
        );
    }

    @EventHandler
    public void balanceChangeEvent(UserBalanceUpdateEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        BigDecimal diff = event.getNewBalance().subtract(event.getOldBalance());
        boolean withdrawal = diff.compareTo(BigDecimal.ZERO)<0;
        int value = Math.abs(diff.intValue());
        int balanceInt = event.getNewBalance().intValue();
        TransactionType type = null;
        this.addTransaction(uuid, withdrawal, value, balanceInt, type);
        if(event.getCause()== UserBalanceUpdateEvent.Cause.COMMAND_ECO){
            type = TransactionType.VOTE;
        } else if (event.getCause()== UserBalanceUpdateEvent.Cause.COMMAND_PAY){
            type = TransactionType.PAY;
        }
        if(type!=null) this.addTransaction(uuid, withdrawal, value, balanceInt, type);
    }

    @EventHandler
    public void playerShopEvent(ShopPurchaseEvent event){
        if(!event.getPurchaser().equals(event.getShop().getOwner())){
            int total = (int) Math.floor(event.getTotal());
            this.addTransaction(event.getPurchaser(), true, total, (int) Main.econ.getBalance(this.plugin.getServer().getOfflinePlayer(event.getPurchaser())), TransactionType.SHOP);
            this.addTransaction(event.getShop().getOwner(), false, total, (int) Main.econ.getBalance(this.plugin.getServer().getOfflinePlayer(event.getShop().getOwner())), TransactionType.SHOP);
        }
    }

    public void addTransaction(UUID player, boolean withdraw, int value, int balance, @Nullable TransactionType type){
        if(!transactions.containsKey(player)){
            transactions.put(player, new ArrayList<>());
        }
        if(type!=null){
            // cancel out first event that fired (uncategorized)
            transactions.get(player).add(new Transaction(
                    withdraw,
                    0,
                    0,
                    0,
                    0,
                    -value,
                    balance
            ));
        }
        transactions.get(player).add(new Transaction(
                withdraw,
                type==TransactionType.VOTE ? value : 0,
                type==TransactionType.GEM ? value : 0,
                type==TransactionType.SHOP ? value : 0,
                type==TransactionType.PAY ? value : 0,
                type==null ? value : 0,
                balance
        ));
    }

    public String getTableName(){
        final String prefix = Objects.requireNonNull(this.plugin.getConfig().getString("mariadb.prefix"));
        return prefix+"balances";
    }

    private String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        for (String line = br.readLine(); line != null; line = br.readLine())
            out.append(line);
        br.close();
        return out.toString();
    }

    public void flush() throws SQLException {
        for (UUID uuid:this.transactions.keySet()) {
            int vote = 0;
            int gem = 0;
            int shop = 0;
            int pay = 0;
            int other = 0;
            int balance = 0;
            for (Transaction transaction:this.transactions.get(uuid)) {
                int multiplier = transaction.withdraw ? -1 : 1;
                vote+=transaction.voting * multiplier;
                gem+=transaction.gems * multiplier;
                shop+=transaction.shops * multiplier;
                pay+=transaction.pay * multiplier;
                other+=transaction.others * multiplier;
                balance= transaction.balance;
            }
            // get last record
            Statement st = this.connection.createStatement();
            ResultSet currentDay = st.executeQuery("SELECT shops,pay,votes,gems,others FROM "+this.getTableName()+" WHERE `uuid`=\""+ uuid +"\" AND `date`=CURRENT_DATE() ORDER BY `date` DESC LIMIT 1");
            if(currentDay.next()){
                // update
                shop+=currentDay.getInt(1);
                pay+=currentDay.getInt(2);
                vote+=currentDay.getInt(3);
                gem+=currentDay.getInt(4);
                other+=currentDay.getInt(5);
                st.close();

                other-=(vote+gem+shop+pay+other)-balance;

                String values = "";
                values+="`shops`="+String.valueOf(shop)+",";
                values+="`pay`="+String.valueOf(pay)+",";
                values+="`votes`="+String.valueOf(vote)+",";
                values+="`gems`="+String.valueOf(gem)+",";
                values+="`others`="+String.valueOf(other);

                Statement update = this.connection.createStatement();
                update.executeUpdate("UPDATE "+this.getTableName()+" SET "+values+" WHERE `uuid`=\""+uuid+"\" AND `date`=CURRENT_DATE()");
                update.close();

            } else {
                st.close();
                Statement lastDaySt = this.connection.createStatement();
                ResultSet lastDay = lastDaySt.executeQuery("SELECT shops,pay,votes,gems,others FROM "+this.getTableName()+" WHERE `uuid`=\""+ uuid +"\" ORDER BY `date` DESC LIMIT 1");
                if(lastDay.next()){
                    // a day exists
                    shop+=lastDay.getInt(1);
                    pay+=lastDay.getInt(2);
                    vote+=lastDay.getInt(3);
                    gem+=lastDay.getInt(4);
                    other+=lastDay.getInt(5);
                }

                lastDay.close();

                // completely new player
                other-=(vote+gem+shop+pay+other)-balance;

                String values = "";
                values+="'"+uuid.toString()+"',";
                values+=String.valueOf(shop)+",";
                values+=String.valueOf(pay)+",";
                values+=String.valueOf(vote)+",";
                values+=String.valueOf(gem)+",";
                values+=String.valueOf(other)+",";
                values+="CURRENT_TIMESTAMP()";

                Statement insert = this.connection.createStatement();
                insert.executeUpdate("INSERT INTO "+this.getTableName()+" (uuid,shops,pay,votes,gems,others,date) VALUES ("+values+")");
                insert.close();
            }
        }
        this.transactions.clear();
    }


}
