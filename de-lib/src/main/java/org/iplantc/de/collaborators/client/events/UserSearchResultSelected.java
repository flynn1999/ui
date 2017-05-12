package org.iplantc.de.collaborators.client.events;

import org.iplantc.de.client.models.collaborators.Subject;
import org.iplantc.de.collaborators.client.events.UserSearchResultSelected.UserSearchResultSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * An event that is fired when user selects a user from user search field
 * 
 * @author sriram,Paul
 * 
 */
public class UserSearchResultSelected extends GwtEvent<UserSearchResultSelectedEventHandler> {

    public interface UserSearchResultSelectedEventHandler extends EventHandler {

        void onUserSearchResultSelected(UserSearchResultSelected userSearchResultSelected);
    }

    public interface HasUserSearchResultSelectedEventHandlers {
        HandlerRegistration addUserSearchResultSelectedEventHandler(UserSearchResultSelectedEventHandler handler);
    }

    public static final GwtEvent.Type<UserSearchResultSelectedEventHandler> TYPE = new GwtEvent.Type<UserSearchResultSelected.UserSearchResultSelectedEventHandler>();
    private final Subject subject;

    public UserSearchResultSelected(Subject subject) {
        this.subject = subject;
    }

    @Override
    public GwtEvent.Type<UserSearchResultSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserSearchResultSelectedEventHandler handler) {
        handler.onUserSearchResultSelected(this);
    }

    public Subject getSubject() {
        return subject;
    }
}
