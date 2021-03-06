package org.iplantc.de.teams.client;

import org.iplantc.de.client.models.groups.Group;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

/**
 * An interface for the UI's Team view in the Collaboration window
 */
public interface TeamsView extends IsWidget {

    /**
     * An appearance class for all string related items in the Teams view
     */
    interface TeamsViewAppearance {

        String leaveTeamSuccess(String teamName);

        String leaveTeamFail();

        String deleteTeamSuccess(String teamName);

        String joinTeamSuccess(String teamName);

        String requestToJoinSubmitted(String teamName);

        SafeHtml editTeamHelpText();

        String teamsHelp();

        int windowMinWidth();

        int windowMinHeight();

        String windowHeading();

        String windowWidth();

        String windowHeight();
    }

    /**
     * This presenter is responsible for managing all the events from the TeamsView
     */
    @JsType
    interface Presenter {

        /**
         * Returns the view
         * @return
         */
        @JsIgnore
        TeamsView getView();

        /**
         * Show the check box column in the teams grid, so that users can select multiple
         * teams at once
         */
        void showCheckBoxes();

        /**
         * Return the list of selected teams from the view
         * @return
         */
        @JsIgnore
        List<Group> getSelectedTeams();

        /**
         * Set the base ID for the view
         * @param baseID
         */
        void setViewDebugId(String baseID);

        /**
         * Gets called when a team name is selected in TeamView
         * @param teamSpl
         */
        @SuppressWarnings("unusable-by-js")
        void onTeamNameSelected(Splittable teamSpl);

        /**
         * Fetches the list of teams the user belongs to with the given filter
         * or search term
         */
        void getTeams(String filter, String searchTerm);

        /**
         * Fires when the user clicks the button to create a new team
         */
        void onCreateTeamSelected();

        /**
         * Fires when the user changes which teams are currently selected
         * @param teamList
         */
        @SuppressWarnings("unusable-by-js")
        void onTeamSelectionChanged(Splittable teamList);
    }

    void setBaseId(String baseId);

    void mask();

    void unmask();

    void showCheckBoxes();

    void setTeamList(Splittable teamListSpl);

    void setSelectedTeams(Splittable teamList);
}
