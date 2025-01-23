package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.shirk.antirelog.combat.CombatPlayer;

@Getter
public class CombatEndEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final CombatPlayer combatPlayer;

    public CombatEndEvent(CombatPlayer combatPlayer) {
        super();
        this.combatPlayer = combatPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
