package org.iplantc.de.theme.base.client.applications.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppIntegratorCell;
import org.iplantc.de.theme.base.client.applications.AppSearchHighlightAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AppIntegratorCellDefaultAppearance implements AppIntegratorCell.AppIntegratorCellAppearance {

    private final AppSearchHighlightAppearance highlightAppearance;

    public AppIntegratorCellDefaultAppearance() {
        this(GWT.<AppSearchHighlightAppearance> create(AppSearchHighlightAppearance.class));
    }

    AppIntegratorCellDefaultAppearance(final AppSearchHighlightAppearance highlightAppearance) {
        this.highlightAppearance = highlightAppearance;
    }

    @Override
    public void render(SafeHtmlBuilder sb, String value, String pattern) {
        SafeHtml highlightText = highlightAppearance.highlightText(value, pattern);
        sb.append(highlightText);
    }
}
