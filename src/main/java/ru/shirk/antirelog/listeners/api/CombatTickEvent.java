package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.shirk.antirelog.combat.CombatPlayer;

@Getter
@RequiredArgsConstructor
public class CombatTickEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final int prevTime;
    private final int newTime;
    private final CombatPlayer combatPlayer;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
