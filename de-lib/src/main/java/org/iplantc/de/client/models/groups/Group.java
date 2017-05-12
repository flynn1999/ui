package org.iplantc.de.client.models.groups;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.collaborators.Subject;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Autobean currently used to represent a Group as defined in the iplant-groups POST /groups endpoint
 *
 * @author aramsey
 */
public interface Group extends Subject, HasDescription {
    
    String getType();
    void setType(String type);

    @AutoBean.PropertyName("display_name")
    String getDisplayName();

    String getExtension();

}
