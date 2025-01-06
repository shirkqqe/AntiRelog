package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.combat.CombatPlayer;

@Getter
@RequiredArgsConstructor
public class CombatPreStartEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final @Nullable CombatPlayer initiator;
    private final @Nullable CombatPlayer damaged;
    private final @NonNull Cause cause;
    private final int timer;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public enum Cause {
        DAMAGE,
        FORCE;
    }
}
