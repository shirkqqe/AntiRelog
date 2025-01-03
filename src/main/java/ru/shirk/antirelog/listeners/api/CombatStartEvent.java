package ru.shirk.antirelog.listeners.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shirk.antirelog.combat.CombatPlayer;

@Getter
@RequiredArgsConstructor
public class CombatStartEvent extends Event {

    private final @NonNull Cause cause;
    private final @Nullable CombatPlayer initiator;
    private final @Nullable CombatPlayer damaged;
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public enum Cause {
        DAMAGE,
        FORCE;
    }
}
