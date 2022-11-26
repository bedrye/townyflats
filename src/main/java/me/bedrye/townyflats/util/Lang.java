package me.bedrye.townyflats.util;

import me.bedrye.townyflats.Townyflats;

import java.util.Objects;

public class Lang {
    public String incorrect_command;
    public String command_claim_true;
    public String command_buy_true;
    public String command_buy_false;
    public String command_buy_nothing;
    public String command_sell_true;
    public String command_sell_false;
    public String command_sell_nothing;
    public String command_delete_true;
    public String command_delete_nothing;
    public String command_info_nothing;
    public String command_info_pos1;
    public String command_info_pos2;
    public String command_info_owner;
    public String command_info_sell;
    public String command_info_sell_button;
    public String command_info_roommates;
    public String command_info_roommates_button;
    public String command_limit_reached;
    public String command_antispam;
    public String command_list;
    public String hologram_1;
    public String hologram_4;
    public String already_apartment_here;
    public String clear_pos, money_symbol;

    public void initialLoad(){
        incorrect_command = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("incorrect_command")).toString();
        command_claim_true = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_claim_true")).toString();
        command_buy_true = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_buy_true")).toString();
        command_buy_false = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_buy_false")).toString();
        command_buy_nothing = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_buy_nothing")).toString();
        command_sell_true = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_sell_true")).toString();
        command_sell_false = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_sell_false")).toString();
        command_sell_nothing = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_sell_nothing")).toString();
        command_delete_true = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_delete_true")).toString();
        command_delete_nothing = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_delete_nothing")).toString();
        command_info_nothing = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_nothing")).toString();
        command_info_pos1 = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_pos1")).toString();
        command_info_pos2 = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_pos2")).toString();
        command_info_owner = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_owner")).toString();
        command_info_sell = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_sell")).toString();
        command_info_sell_button = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_sell_button")).toString();
        command_info_roommates = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_roommates")).toString();
        command_info_roommates_button = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_info_roommates_button")).toString();
        command_limit_reached = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_limit_reached")).toString();
        command_antispam = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_antispam")).toString();
        command_list = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("command_list")).toString();
        hologram_1 = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("hologram_1")).toString();
        hologram_4 = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("hologram_4")).toString();
        already_apartment_here = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("already_apartment_here")).toString();
        clear_pos = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("clear_pos")).toString();
        money_symbol = Objects.requireNonNull(Townyflats.getInstance().getConfig().get("money_symbol")).toString();
    }


}
