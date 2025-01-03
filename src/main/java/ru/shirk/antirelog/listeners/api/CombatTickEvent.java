package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.shirk.antirelog.combat.CombatPlayer;

public class CombatTickEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final int prevTime;
    private final int newTime;
    private final CombatPlayer combatPlayer;

    public CombatTickEvent(int prevTime, int newTime, CombatPlayer combatPlayer) {
        this.prevTime = prevTime;
        this.newTime = newTime;
        this.combatPlayer = combatPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
