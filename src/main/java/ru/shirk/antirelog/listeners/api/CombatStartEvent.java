package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.combat.CombatPlayer;

@Getter
public class CombatStartEvent extends Event {

    private final @NonNull CombatPreStartEvent.Cause cause;
    private final @Nullable CombatPlayer initiator;
    private final @Nullable CombatPlayer damaged;
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    public CombatStartEvent(@NonNull CombatPreStartEvent.Cause cause, @Nullable CombatPlayer initiator, @Nullable CombatPlayer damaged) {
        super(true);
        this.cause = cause;
        this.initiator = initiator;
        this.damaged = damaged;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
