//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mcsunnyside.tpsalert;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    private final String name = Bukkit.getServer().getClass().getPackage().getName();
    private final String version;
    private final DecimalFormat format;
    private Object serverInstance;
    private Field tpsField;
    public String lastTPSstatus;
    public String Unknown;
    public String Good;
    public String Warning;
    public String Bad;

    public Main() {
        this.version = this.name.substring(this.name.lastIndexOf(46) + 1);
        this.format = new DecimalFormat("##.##");
        this.lastTPSstatus = null;
        this.Unknown = null;
        this.Good = null;
        this.Warning = null;
        this.Bad = null;
    }

    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        try {
            this.serverInstance = MinecraftServer.getServer();
            this.tpsField = this.serverInstance.getClass().getField("recentTps");
        } catch (SecurityException | IllegalArgumentException | NoSuchFieldException var2) {
            var2.printStackTrace();
        }

        this.Unknown = this.getConfig().getString("strings.Unknown");
        this.Good = this.getConfig().getString("strings.Good");
        this.Warning = this.getConfig().getString("strings.Warning");
        this.Bad = this.getConfig().getString("strings.Bad");
        this.lastTPSstatus = this.Unknown;
        new Metrics(this);
        (new BukkitRunnable() {
            public void run() {
                double currentTPS = Double.parseDouble(Main.this.getTPS(0));
                String currentTPSstatus = Main.this.Unknown;
                if (currentTPS >= 18.25D) {
                    currentTPSstatus = Main.this.Good;
                } else if (currentTPS >= 17.49D) {
                    currentTPSstatus = Main.this.Warning;
                } else {
                    currentTPSstatus = Main.this.Bad;
                }

                if (!Objects.equals(Main.this.lastTPSstatus, currentTPSstatus)) {
                    Main.this.printTPSMessage(Main.this.lastTPSstatus, currentTPSstatus);
                }

                Main.this.lastTPSstatus = currentTPSstatus;
            }
        }).runTaskTimerAsynchronously(this, (long)this.getConfig().getInt("settings.checktime"), (long)this.getConfig().getInt("settings.checktime"));
    }

    public void printTPSMessage(String lastTPS, String currentTPS) {
        List<String> list = new ArrayList();
        list.add(this.getConfig().getString("message.TPSChanged") + lastTPS + this.getConfig().getString("message.Arrow") + currentTPS);
        list.add(this.getConfig().getString("message.Advice") + this.printTips(currentTPS));
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            Bukkit.broadcastMessage(string);
        }

    }

    public String printTips(String level) {
        if (Objects.equals(level, this.Unknown)) {
            return this.getConfig().getString("advice.Unknown");
        } else if (Objects.equals(level, this.Good)) {
            return this.getConfig().getString("advice.Good");
        } else if (Objects.equals(level, this.Warning)) {
            return this.getConfig().getString("advice.Warning");
        } else {
            return Objects.equals(level, this.Bad) ? this.getConfig().getString("advice.Bad") : "Error";
        }
    }

    public String getTPS(int time) {
        try {
            double[] tps = (double[])this.tpsField.get(this.serverInstance);
            return this.format.format(tps[time]);
        } catch (IllegalAccessException var3) {
            throw new RuntimeException(var3);
        }
    }
}
