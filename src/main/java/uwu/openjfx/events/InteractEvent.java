package uwu.openjfx.events;

import com.almasb.fxgl.entity.Entity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.event.Event;
import javafx.event.EventType;

public class InteractEvent extends Event {

    private static final long serialVersionUID = 1L;
    public static final EventType<InteractEvent> ANY
            = new EventType<>(Event.ANY, "Interact_Event");
    public static final EventType<InteractEvent> NPC
            = new EventType<>(ANY, "NPC");

    private transient Entity entity;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "FXGL event bus shares live entities by design.")
    public InteractEvent(EventType<? extends Event> eventType, Entity entity) {
        super(eventType);
        this.entity = entity;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "FXGL event listeners require direct entity access.")
    public Entity getEntity() {
        return entity;
    }

}
